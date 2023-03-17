package com.GLAS.LakeDistrictNavigation

import android.content.Context
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SurveyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

data class SurveyAnswers(var age:String = "", var gender:String = "",var group:String = "",var employment:String = "",var arrive:String = "")
class SurveyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var NewSurveyAnswer : SurveyAnswers
    lateinit var endSurvey : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey, container, false)

            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NewSurveyAnswer = SurveyAnswers()


        val openSurvey : Button = view.findViewById(R.id.startSurveyButton)
        val reOpenSurvey : Button = view.findViewById(R.id.restartSurveyButton)
        endSurvey = view.findViewById(R.id.endSurveyButton)
        val surveySheet : ScrollView = view.findViewById(R.id.surveySheet)
        val surveyThanks : CardView = view.findViewById(R.id.surveyThanks)
        val surveyMessage : TextView = view.findViewById(R.id.surveyMessage)
        val deleateSurvey : Button = view.findViewById(R.id.deleteSurveyAnsers)

        if (checkForFilledSurvey(view)){
            surveyThanks.visibility = View.VISIBLE
            reOpenSurvey.visibility = View.VISIBLE
            openSurvey.visibility = View.GONE
            deleateSurvey.visibility = View.VISIBLE
            surveyMessage.text = "You have already filled in the survey, but can change you answers"
        }


        openSurvey.setOnClickListener(){
            surveySheet.visibility = View.VISIBLE
            openSurvey.visibility = View.GONE
            checkSurvey()
        }

        reOpenSurvey.setOnClickListener(){
            surveySheet.visibility = View.VISIBLE
            reOpenSurvey.visibility = View.GONE
            surveyThanks.visibility = View.GONE
            reOpenSurvey.visibility = View.GONE
            openSurvey.visibility = View.GONE
            deleateSurvey.visibility = View.GONE
            checkSurvey()

        }

        deleateSurvey.setOnClickListener(){
            deleteSurvey(view)
            openSurvey.visibility = View.VISIBLE
            surveyThanks.visibility = View.GONE
            reOpenSurvey.visibility = View.GONE

            deleateSurvey.visibility = View.GONE
        }

        endSurvey.setOnClickListener(){
            surveySheet.visibility = View.GONE
            surveyThanks.visibility = View.VISIBLE
            reOpenSurvey.visibility = View.VISIBLE
            deleateSurvey.visibility = View.VISIBLE
            saveSurvey(view)
        }

        val AgeRadioGroup  : MultiLineRadioGroup = view.findViewById(R.id.radioAge)
        AgeRadioGroup.setOnCheckedChangeListener(MultiLineRadioGroup.OnCheckedChangeListener { group, button ->
            Log.v("Radio", button.text.toString() )
            NewSurveyAnswer.age = button.text.toString()
            checkSurvey()
        })

        val GenderRadioGroup  : MultiLineRadioGroup = view.findViewById(R.id.radioGender)
        GenderRadioGroup.setOnCheckedChangeListener(MultiLineRadioGroup.OnCheckedChangeListener { group, button ->
            Log.v("Radio", button.text.toString() )
            NewSurveyAnswer.gender = button.text.toString()
            checkSurvey()
        })

        val GroupRadioGroup  : MultiLineRadioGroup = view.findViewById(R.id.radioTravel)
        GroupRadioGroup.setOnCheckedChangeListener(MultiLineRadioGroup.OnCheckedChangeListener { group, button ->
            Log.v("Radio", button.text.toString() )
            NewSurveyAnswer.group = button.text.toString()
            checkSurvey()
        })

        val ArriveRadioGroup  : MultiLineRadioGroup = view.findViewById(R.id.radioArrive)
        ArriveRadioGroup.setOnCheckedChangeListener(MultiLineRadioGroup.OnCheckedChangeListener { group, button ->
            Log.v("Radio", button.text.toString() )
            NewSurveyAnswer.arrive = button.text.toString()
            checkSurvey()
        })

        val EmploymentRadioGroup  : MultiLineRadioGroup = view.findViewById(R.id.radioEmployment)
        EmploymentRadioGroup.setOnCheckedChangeListener(MultiLineRadioGroup.OnCheckedChangeListener { group, button ->
            Log.v("Radio", button.text.toString() )
            NewSurveyAnswer.employment = button.text.toString()
            checkSurvey()
        })
    }

    fun checkSurvey()
    {
        if (NewSurveyAnswer.age != "" && NewSurveyAnswer.gender != "" && NewSurveyAnswer.arrive != "" && NewSurveyAnswer.arrive != ""&& NewSurveyAnswer.group != "" ){
            endSurvey.isEnabled = true
        }
        else
        {
            endSurvey.isEnabled = false
        }
    }

    fun saveSurvey(view: View)
    {
        var context =  view.context

        val fileContents = NewSurveyAnswer.age +"_"+ NewSurveyAnswer.gender +"_"+ NewSurveyAnswer.group +"_"+ NewSurveyAnswer.employment +"_"+ NewSurveyAnswer.arrive
        var fileName = "Survey_Filled"

        Log.v("saveload", fileContents)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun deleteSurvey(view: View)
    {
        var fileName = "Survey_Filled"
        view.context.deleteFile(fileName)
    }


    fun checkForFilledSurvey(view: View) : Boolean{
        var context =  view.context
        var files: Array<String> = context.fileList()

        if (files.isNotEmpty()){
            for (e in files){
//
                if (e.startsWith("Survey_Filled")){
                    return true
                }
            }
        }
        return false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SurveyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SurveyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}