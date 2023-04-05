package com.GLAS.LakeDistrictNavigation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.GLAS.LakeDistrictNavigation.MainActivity
import com.GLAS.LakeDistrictNavigation.R
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var reqestPermissions: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        reqestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted : Boolean ->
            if (isGranted){
                LoadMainFragment()
                Log.v("Permsions","IsGranted")
            }
            else{
                LoadMainFragment()
                Log.v("Permsions","NotGranted")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ranger_splash, container, false)


    }

    private fun hideSystemBars(window : Window, view: View) {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return

        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }

    private fun ShowSystemBars(window : Window, view: View) {
        val windowInsetsController =
            WindowCompat.getInsetsController(window,view) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.show(WindowInsetsCompat.Type.navigationBars())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //hideSystemBars(requireActivity().window, view)


        (activity as MainActivity).hideNavView()


        val constrainButton : ConstraintLayout = view.findViewById(R.id.welcome_layout)
        constrainButton.setOnClickListener(){
            //Requsest Location
            checkLocationPermission()


//            val action = BlankFragmentDirections.actionBlankFragmentToMapBoxFragment()
//            view.findNavController().navigate(action)
//            (activity as MainActivity).showNavView()
        }
    }

    private fun LoadMainFragment(){
        val action = BlankFragmentDirections.actionBlankFragmentToMapBoxFragment()
        requireView().findNavController().navigate(action)
        (activity as MainActivity).showNavView()
    }


    private fun checkLocationPermission() {

        when {requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED -> {
                    //Just carry on
                Log.v("Permsions","Granted")
                    LoadMainFragment()
                }

            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)
            -> {
                //Show Rational
                Log.v("Permsions","Show Rational")
                tellAboutPermsions()
            }
            else -> {
                //Not Requested
                Log.v("Permsions","No Responce")
                tellAboutPermsions()
            }
        }
    }

    private fun tellAboutPermsions(){
        var viewToShow = layoutInflater.inflate( R.layout.ask_for_permsions, null)
        AlertDialog.Builder(requireContext())

            .setView(viewToShow)
            .setPositiveButton(
                "OK"
            ) { _, _ ->
                //Prompt the user once explanation has been shown
                reqestPermissions.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNeutralButton("Maybe Later"){ _, _ ->
                //Prompt the user once explanation has been shown
                LoadMainFragment()
            }
            .create()
            .show()
    }


}