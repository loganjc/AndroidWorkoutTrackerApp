package com.example.workoutapp.ui.walkrun

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.workoutapp.R
import com.example.workoutapp.WorkoutApplication
import com.example.workoutapp.data.Workout
import com.example.workoutapp.databinding.FragmentWalkRunBinding
import com.example.workoutapp.ui.calendar.WorkoutViewModel
import com.example.workoutapp.ui.calendar.WorkoutViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

class WalkRunFragment: Fragment(), SensorEventListener  {
    private val TAG = "WALK_RUN_FRAGMENT"
    private var _binding: FragmentWalkRunBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    //Location attributes -------------------------------
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    //Lat, Lon, Distance, Time, button --------------------
    private var isRunning = false
    private var currentLat = 0.0
    private var currentLon = 0.0
    private var distance = 0.0 //This is in meters!
    private var stepCount = 0
    //Timer attributes -----------------------------------
    private lateinit var chronometer: Chronometer
    private var elapsedTime: Long = 0
    private var workoutDurationEnd: Long = 0
    private var isTimerRunning = false
    //Step counter attributes ----------------------------
    private var sensorManager: SensorManager? = null
    // Accelerometer Attributes --------------------------
    private var magnitudePrevious = 0
    // Data Layer Interface
    private val viewModel : WorkoutViewModel by activityViewModels {
        WorkoutViewModelFactory((activity?.application as WorkoutApplication).database.workoutDao())
    }
    lateinit var workout: Workout


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    //----------------------------------= LIFE CYCLE =-----------------------------------------------
    // Fragment LifeCycle Methods

    //Inflates view, loads in savedInstanceState if !null
    @SuppressLint("MissingPermission") //Function 'checkPermissions' serves as a check.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkRunBinding.inflate(inflater, container, false)
        val root: View = binding.root

        updateValuesFromBundle(savedInstanceState) //updates data stored in onPause()
        return root
    }

    @SuppressLint("MissingPermission", "NewApi") //running in a limited environ with guaranteed SDK version
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //create metric tool elements
        setUpLocationStuff()
        setUpSensor()
        chronometer = binding.simpleChronometer

        //set up button listeners
        binding.walkrunButtonStartPause.setOnClickListener{
            startRunWalk()
        }
        binding.walkrunButtonEnd.setOnClickListener{
            stopRunWalk()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback) //stops location updates when not on run/walk
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "Fragment destroyed")
    }

    //-------------------------------= SAVE STATE =-------------------------------------------------
    // Save state methods

    override fun onSaveInstanceState(outState: Bundle) {
        outState?.putDouble("key_distance", distance)
        outState?.putInt("key_stepCount", stepCount)
        outState?.putLong("key_elapsedTime", elapsedTime)
        super.onSaveInstanceState(outState)
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        if (savedInstanceState.keySet().contains("key_distance")) {
            distance = savedInstanceState.getDouble("key_distance")
            binding.walkrunTextViewDistance.text = distance.toString()
        }
        if (savedInstanceState.keySet().contains("key_stepCount")) {
            stepCount = savedInstanceState.getInt("key_stepCount")
            binding.walkrunTextViewSteps.text = stepCount.toString()
        }
        if (savedInstanceState.keySet().contains("key_elapsedTime")) {
            elapsedTime = savedInstanceState.getLong("key_elapsedTime")
            binding.simpleChronometer.text = elapsedTime.toString()
        }
    }

    //----------------------------------= LOCATION =------------------------------------------------
    // Fused Location Provider Methods

    private fun setUpLocationStuff() {
        //Init Fused Location Provider, set up location requests, set up callback
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationCallback = object : LocationCallback() {
            //IMPORTANT: updates variables when updates received from
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                val newLocation = locationResult.lastLocation
                Log.d(TAG, "onLocationResult: new location received.")
                if (isRunning and (newLocation!= null)) {
                    val newLat = newLocation!!.latitude
                    val newLon = newLocation.longitude
                    var distanceArray = floatArrayOf(0.toFloat())
                    Location.distanceBetween(currentLat, currentLon, newLat, newLon, distanceArray)
                    distance += distanceArray[0]
                    currentLat = newLat
                    currentLon = newLon
                    binding.walkrunTextViewDistance.text = distance.toString()
                    Log.d(TAG, "onLocationResult: distance calc successful, distance = " + distanceArray[0].toString())
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        Log.d(TAG, "startLocationUpdates successful")
    }

    //Creates and starts location updates
    private fun createLocationRequest() {
        locationRequest = com.google.android.gms.location.LocationRequest.Builder(3000).setPriority(LocationRequest.QUALITY_HIGH_ACCURACY).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener{
            startLocationUpdates()
            Log.d(TAG, "createLocationRequest successful")
        }
        task.addOnFailureListener{e ->
            if(e is ResolvableApiException) {
                try {
                } catch (sendEX: IntentSender.SendIntentException) {
                    //do nothing
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
    }

    //---------------------------------= TIMER =----------------------------------------------------

    private fun startTimer() {
        if (!isTimerRunning) {
            chronometer.base = (SystemClock.elapsedRealtime()- elapsedTime)
            chronometer.start()
            isTimerRunning = true
        }
    }

    private fun pauseTimer() {
        if (isTimerRunning) {
            chronometer.stop()
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.base
            isTimerRunning = false
        }
    }

    private fun stopTimer() {
        pauseTimer()
        workoutDurationEnd = elapsedTime
        elapsedTime = 0
        chronometer.base = SystemClock.elapsedRealtime()
        Log.d(TAG, "Timer stopped. Saved time is $workoutDurationEnd")
    }

    //-------------------------------------= SENSOR =-----------------------------------------------
    //Sensor methods

    private fun setUpSensor() {
        Log.d(TAG, "Starting setUpSensor()....")
        sensorManager = requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
        if (sensorManager == null) {
            Log.e(TAG, "sensorManager is null!")
        }

        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if(accelerometer == null) {
            Log.e(TAG, "accelerometer is null :(")
        } else {
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            Log.d(TAG, "Sensor manager listener registered successfully")
        }
    }

    //Calculates when momentum via Pythagorean theorem goes over certain threshold to measure steps.
    override fun onSensorChanged(event: SensorEvent?) {
        //Log.d(TAG, "Sensor event detected")
        if (event != null) {
            var x_acceleration = event.values[0]
            var y_acceleration = event.values[1]
            var z_acceleration = event.values[2]

            var magnitude = sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration)
            var magnitudeDelta = magnitude - magnitudePrevious
            magnitudePrevious = magnitude.toInt()

            if (magnitudeDelta > 3) {
                stepCount++
                binding.walkrunTextViewSteps.text = stepCount.toString()
                Log.d(TAG, "Sensor event step count incremented")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //do nothing
    }

    //---------------------------------= UI =-------------------------------------------------------
    // UI Button methods

    //called by user tapping start/pause button. gets location data, starts timer + steps, updates textViews.
    private fun startRunWalk() {
        isRunning = !isRunning
        if (isRunning) {
            createLocationRequest()
            startTimer()
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            pauseTimer()
        }
        Log.d(TAG, "user clicked start/pause button")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopRunWalk() {
        isRunning = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopTimer()
        //add data to a Room DB object. Send to DB via DAO
        //Reset duration, steps, distance fields.
        Log.d(TAG, "user clicked stop button")
        saveWorkout()
        stepCount = 0
        distance = 0.0
    }

    //-----------------------------------= Room DB =------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveWorkout() {
        if (workoutDurationEnd > 0) {
            val formattedDuration = String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(workoutDurationEnd),
                TimeUnit.MILLISECONDS.toSeconds(workoutDurationEnd) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(workoutDurationEnd))
            )
            //Add workout to database
            viewModel.addNewWorkout(formattedDuration, stepCount, distance.toInt())
            Log.d(TAG, "Workout data saved successfully!")
        }
    }
}