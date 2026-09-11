package com.local.amjbc.chandacal

import android.app.AlertDialog
import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.local.amjbc.R
import java.text.DecimalFormat
import java.util.*

class ChandaCal : Fragment() {

    var tw_netincome: TextWatcher? = null
    var tw_main: TextWatcher? = null
    var tw_jsalana: TextWatcher? = null
    var tw_otherChandas: TextWatcher? = null
    var et_netincome: EditText? = null
    var et_mainamount: EditText? = null
    var et_otheramount: EditText? = null
    var auxamount: TextView? = null
    var jsalanaamount: TextView? = null
    var mainamount: TextView? = null
    var aam: TextView? = null
    var nettotalamount: TextView? = null
    var annualtotalamount: TextView? = null
    var netremamount: TextView? = null
    var swMoosi: SwitchCompat? = null
    var spOrg: Spinner? = null
    var spWasiyyatRate: Spinner? = null
    var spIncomeStatus: Spinner? = null
    var llWasiyyatRate: View? = null
    var llIncomeStatus: View? = null
    var aux: TextView? = null
    var netincome: TextView? = null
    var bal: TextView? = null
    var marquee: TextView? = null
    var save: Button? = null
    var otherChandas: Button? = null
    var moreInfo: Button? = null
    var donation_button: Button? = null
    var sp: SharedPreferences? = null
    var cb_yearly: CheckBox? = null
    var cb_monthly: CheckBox? = null
    var roundup: Boolean = false
    var autocalculate: Boolean = false
    var moosi: Boolean = false
    var monthly: Boolean = false
    var income: Double = 0.0
    var chanda_main: Double = 0.0
    var chanda_jsalana: Double = 0.0
    var chanda_aux: Double = 0.0
    var net_total: Double = 0.0
    var annual_total: Double = 0.0
    var rem_total: Double = 0.0
    var org: String? = null
    var wasiyyateRate: Double = 0.1
    var incomeStatus: String = "Earner"
    var other = 0
    var database: SQLiteDatabase? = null
    var cds: ChandaDataSource? = null
    var helper: MySqlLiteHelper? = null
    var reciever1: BroadcastReceiver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_chanda_calc, container, false)
        setHasOptionsMenu(true)

        aam = rootView.findViewById<View>(R.id.main) as TextView
        aux = rootView.findViewById<View>(R.id.aux) as TextView
        donation_button = rootView.findViewById<View>(R.id.dontation_button) as Button
        netincome = rootView.findViewById<View>(R.id.textView1) as TextView
        marquee = rootView.findViewById<View>(R.id.MarqueeTextCalFragment) as TextView
        
        mainamount = rootView.findViewById<View>(R.id.mainamount_text) as TextView
        jsalanaamount = rootView.findViewById<View>(R.id.jsalanaamount_text) as TextView
        auxamount = rootView.findViewById<View>(R.id.auxamount_text) as TextView

        val tf3 = Typeface.createFromAsset(activity!!.assets, "Ubahn.ttf")
        netincome!!.typeface = tf3
        //bal.setTypeface(tf3);

        sp = PreferenceManager.getDefaultSharedPreferences(this.activity)
        other = sp!!.getInt("others", 0)

        cds = ChandaDataSource(activity!!.baseContext)

        roundup = sp!!.getBoolean("roundup", false)
        autocalculate = sp!!.getBoolean("autocalculate", true)
        moosi = sp!!.getBoolean("moosiPre", false)
        monthly = sp!!.getBoolean("monthlyPre", false)
        org = sp!!.getString("org", "Ansaar")


        donation_button!!.setOnClickListener {
            val url = "https://www.amjinc.ca"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }


        setOrganisation()

        tw_otherChandas = object : TextWatcher {
            override fun afterTextChanged(otherAmount: Editable) {
                if (isIntNumber(otherAmount)) {
                    save!!.isEnabled = true
                } else {
                }
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        }

        tw_netincome = object : TextWatcher {
            override fun afterTextChanged(income_amount: Editable) {
                if (isIntNumber(income_amount)) {
                    save!!.isEnabled = true
                    income = income_amount.toString().toDouble()
                    if (income_amount.toString() == "0") save!!.isEnabled = false
                } else income = 0.0

                monthly = sp!!.getBoolean("monthlyPre", false)

                if (monthly) calculatemonthly()
                else calculate()

                populate_fields()
            }
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
        }

        et_netincome = rootView.findViewById<View>(R.id.income) as EditText
        et_netincome!!.addTextChangedListener(tw_netincome)
        et_otheramount = rootView.findViewById<View>(R.id.otheramount) as EditText
        et_otheramount!!.setText(other.toString())
        et_otheramount!!.addTextChangedListener(tw_otherChandas)

        otherChandas = rootView.findViewById<View>(R.id.otherChandas) as Button
        moreInfo = rootView.findViewById<View>(R.id.moreInfo) as Button
        otherChandas!!.addTextChangedListener(tw_otherChandas)
        save = rootView.findViewById<View>(R.id.save) as Button

        nettotalamount = rootView.findViewById<View>(R.id.nettotalamount) as TextView
        annualtotalamount = rootView.findViewById<View>(R.id.annualtotalamount) as TextView
        netremamount = rootView.findViewById<View>(R.id.netremamount) as TextView

        swMoosi = rootView.findViewById<View>(R.id.sw_moosi) as SwitchCompat
        spOrg = rootView.findViewById<View>(R.id.sp_org) as Spinner

        llWasiyyatRate = rootView.findViewById(R.id.ll_wasiyyat_rate)
        spWasiyyatRate = rootView.findViewById(R.id.sp_wasiyyat_rate)
        llIncomeStatus = rootView.findViewById(R.id.ll_income_status)
        spIncomeStatus = rootView.findViewById(R.id.sp_income_status)

        swMoosi!!.setOnCheckedChangeListener { _, isChecked ->
            moosi = isChecked
            val editor = sp!!.edit()
            editor.putBoolean("moosiPre", isChecked)
            editor.apply()
            
            updateUIForMoosi(isChecked)
            calculate()
            populate_fields()
        }

        spOrg!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                org = spOrg!!.selectedItem.toString()
                val editor = sp!!.edit()
                editor.putString("org", org)
                editor.apply()
                
                updateUIForOrg(org!!)
                calculate()
                populate_fields()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spWasiyyatRate!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = spWasiyyatRate!!.selectedItem.toString()
                wasiyyateRate = when {
                    selected.contains("1/3") -> 1.0/3.0
                    selected.contains("1/4") -> 1.0/4.0
                    selected.contains("1/5") -> 1.0/5.0
                    selected.contains("1/6") -> 1.0/6.0
                    selected.contains("1/7") -> 1.0/7.0
                    selected.contains("1/8") -> 1.0/8.0
                    selected.contains("1/9") -> 1.0/9.0
                    else -> 0.1
                }
                calculate()
                populate_fields()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spIncomeStatus!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                incomeStatus = spIncomeStatus!!.selectedItem.toString()
                calculate()
                populate_fields()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val cb_roundup = rootView.findViewById<View>(R.id.amount_roundup) as CheckBox

        if (roundup) cb_roundup.isChecked = true
        else cb_roundup.isChecked = false

        moreInfo!!.setOnClickListener {
            var info: String? = "No information available"
            if (org == "Lajna") info = resources.getString(R.string.chanda_info_Lajna)
            else if (org == "Khudaam") info = resources.getString(R.string.chanda_info_Khuddam)
            else if (org == "Ansaar") info = resources.getString(R.string.chanda_info_Ansar)

            AlertDialog.Builder(activity)
                .setTitle("Info")
                .setMessage(info)
                .setNegativeButton(android.R.string.ok) { dialog, which -> }
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
        }


        save!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val editor = sp!!.edit()
                editor.putString("main", chanda_main.toString())
                editor.putString("jsalana", chanda_jsalana.toString())
                editor.putString("orgz", chanda_aux.toString())
                editor.apply()
                CheckSyncronization()
            }

            private fun CheckSyncronization() {
                if (sp!!.getBoolean("remoteSync", false)) {
                    Toast.makeText(activity!!.baseContext, "Synchronizing...", Toast.LENGTH_LONG).show()
                    activity!!.startService(Intent(activity, SyncService::class.java))
                } else {
                    insertToDB()
                }
            }
        })

        otherChandas!!.setOnClickListener {
            val newFragment = OtherChandas()
            val transaction = fragmentManager!!.beginTransaction()
            activity!!.overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
            transaction.replace(R.id.content_frame, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        cb_roundup.setOnClickListener {
            val editor = sp!!.edit()
            if (cb_roundup.isChecked) {
                Log.v("CA", "roundup true")
                editor.putBoolean("roundup", true)
                roundup = true
            } else {
                Log.v("CA", "roundup false")
                editor.putBoolean("roundup", false)
                roundup = false
            }
            editor.apply()
            populate_fields()
        }

        val filter = IntentFilter("broadcast")
        reciever1 = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                insertToDB()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity!!.registerReceiver(
                reciever1,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            activity!!.registerReceiver(
                reciever1,
                filter
            )
        }
        return rootView

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // detail preferences removed as they are integrated in main UI
        return super.onOptionsItemSelected(item)
    }


    private fun insertToDB() {
        val mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        val sync = sp!!.getString("response", "false")

        try {
            cds!!.open()
            cds!!.insertChanda(
                sp!!.getString("main", "0"), sp!!.getString("jsalana", "0"), sp!!.getString("orgz", "0"), sp!!.getInt("tj", 0),
                sp!!.getInt("wj", 0), sp!!.getInt("ef", 0), sp!!.getInt("fit", 0), sp!!.getInt("sad", 0), mydate, sync
            )

            resetFields()
            Toast.makeText(activity!!.baseContext, "Saved", Toast.LENGTH_LONG).show()
            saveCheck = true

        } catch (e: Exception) {
            Toast.makeText(activity!!.baseContext, e.toString(), Toast.LENGTH_LONG).show()
        }

    }

    fun resetFields() {
        val df = DecimalFormat("$#,##0.00")
        et_netincome!!.setText("0")
        mainamount!!.text = df.format(0)
        jsalanaamount!!.text = df.format(0)
        auxamount!!.text = df.format(0)
        et_otheramount!!.setText("0")
        nettotalamount!!.text = df.format(0)
        annualtotalamount!!.text = df.format(0)
        netremamount!!.text = df.format(0)

        save!!.isEnabled = false
    }

    fun populate_fields() {
        val df = DecimalFormat("$#,##0.00")
        
        val annualMain: Double
        val annualJalsa: Double
        val annualAux: Double
        val annualTotal: Double
        val monthlyTotal: Double

        if (roundup) {
            annualMain = Math.ceil(chanda_main)
            annualJalsa = Math.ceil(chanda_jsalana)
            annualAux = Math.ceil(chanda_aux)
            annualTotal = Math.ceil(annual_total)
            monthlyTotal = Math.ceil(net_total)
        } else {
            annualMain = round(chanda_main, 2)
            annualJalsa = round(chanda_jsalana, 2)
            annualAux = round(chanda_aux, 2)
            annualTotal = round(annual_total, 2)
            monthlyTotal = round(net_total, 2)
        }

        // Breakdown card now shows annual amounts for clarity
        mainamount!!.text = df.format(annualMain)
        jsalanaamount!!.text = df.format(annualJalsa)
        auxamount!!.text = df.format(annualAux)
        
        // Totals
        nettotalamount!!.text = df.format(monthlyTotal)
        annualtotalamount!!.text = df.format(annualTotal)
        
        rem_total = (income) - annualTotal
        netremamount!!.text = df.format(if (rem_total > 0) rem_total else 0.0)
    }

    fun calculatemonthly() {
        calculate()
    }

    fun calculate() {
        // The user treats the input 'income' as the basis for the annual calculation.
        // We calculate all components as annual obligations.
        
        // 1. Main Chanda (Annual)
        if (moosi) {
            chanda_main = income * wasiyyateRate
            aam!!.text = "Chanda Wasiyyat"
        } else {
            chanda_main = income / 16.0
            aam!!.text = "Chanda Aam"
        }

        // 2. Jalsa Salana (Annual - 1/120th of annual income)
        chanda_jsalana = income / 120.0

        // 3. Auxiliary Organization (Annual)
        var annualAux = 0.0
        val monthlyIncome = income / 12.0

        when (org) {
            "Ansaar" -> {
                // Membership: 1% of annual (min $36/year)
                annualAux += Math.max(income * 0.01, 36.0)
                // Ijtima: 2.5% of 1 month (min $24/year)
                annualAux += Math.max(monthlyIncome * 0.025, 24.0)
                // Isha'at: $10/year
                annualAux += 10.0
            }
            "Khudaam" -> {
                if (incomeStatus == "Earner") {
                    annualAux += Math.max(income * 0.01, 36.0)
                    annualAux += Math.max(monthlyIncome * 0.025, 30.0)
                } else {
                    annualAux += 36.0 + 30.0
                }
            }
            "Lajna" -> {
                if (incomeStatus == "Independent Income") {
                    annualAux += Math.max(income * 0.01, 24.0)
                    annualAux += 24.0
                } else {
                    annualAux += Math.max(income * 0.01, 12.0)
                    annualAux += 12.0
                }
            }
        }
        
        chanda_aux = annualAux

        // 4. Totals
        // The sum of these components is the Annual Total
        annual_total = chanda_main + chanda_jsalana + chanda_aux + (other * 12.0)
        
        // Monthly Total is the Annual Total divided by 12
        net_total = annual_total / 12.0
    }

    private fun updateUIForMoosi(isMoosi: Boolean) {
        llWasiyyatRate?.visibility = if (isMoosi) View.VISIBLE else View.GONE
    }

    private fun updateUIForOrg(orgName: String) {
        aux!!.text = orgName
        when (orgName) {
            "Ansaar" -> {
                llIncomeStatus?.visibility = View.GONE
            }
            "Khudaam" -> {
                llIncomeStatus?.visibility = View.VISIBLE
                val adapter = ArrayAdapter.createFromResource(activity!!, R.array.khuddam_status, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spIncomeStatus?.adapter = adapter
            }
            "Lajna" -> {
                llIncomeStatus?.visibility = View.VISIBLE
                val adapter = ArrayAdapter.createFromResource(activity!!, R.array.lajna_status, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spIncomeStatus?.adapter = adapter
            }
        }
    }

    fun isIntNumber(num: Editable): Boolean {
        return try {
            num.toString().toDouble()
            true
        } catch (nfe: NumberFormatException) {
            false
        }
    }

    private fun setOrganisation() {
        if (org == "Lajna") {
            aux!!.text = org
        } else {
            aux!!.text = sp!!.getString("org", "Ansaar")
        }

    }


    override fun onResume() {
        super.onResume()

        if (et_netincome!!.text.toString() == "0") save!!.isEnabled = false

        moosi = sp!!.getBoolean("moosiPre", false)
        org = sp!!.getString("org", "Ansaar")
        other = sp!!.getInt("others", 0)
        et_otheramount!!.setText(other.toString())

        swMoosi?.isChecked = moosi
        updateUIForMoosi(moosi)
        
        val orgArray = resources.getStringArray(R.array.listArray)
        val orgIndex = orgArray.indexOf(org).coerceAtLeast(0)
        spOrg?.setSelection(orgIndex)
        updateUIForOrg(org!!)

        aux!!.text = org
        calculate()
        populate_fields()

        setMoosi()
        retainAmount()
    }

    private fun retainAmount() {
        val ni = sp!!.getString("netIncome", "")
        if (!ni.isNullOrEmpty()) {
            et_netincome!!.setText(ni, TextView.BufferType.EDITABLE)
        }
        mainamount!!.text = sp!!.getString("mainC", "0")
        jsalanaamount!!.text = sp!!.getString("jSalaC", "0")
        auxamount!!.text = sp!!.getString("auxC", "0")

    }

    private fun setMoosi() {
        calculate()
        populate_fields()
    }

    companion object {
        var saveCheck: Boolean = false

        fun round(value: Double, places: Int): Double {
            require(places >= 0)
            var v = value
            val factor = Math.pow(10.0, places.toDouble()).toLong()
            v *= factor
            val tmp = Math.round(v)
            return tmp.toDouble() / factor
        }
    }
}
