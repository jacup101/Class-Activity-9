package com.example.kotlinexample2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class FragmentTwo(val appContext : Context): Fragment() {

    private val viewModel: UserViewModel by activityViewModels()
    private val requestQueue = Volley.newRequestQueue(appContext)
    private val url = "http://api.zippopotam.us/us/";
    private lateinit var textView: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_two,container,false)
        textView = view.findViewById(R.id.text_view_two)

        //call get information, which returns a live data
        //so that we can observe on the this live data to get changes
        //then we want to display info in the ui
        /*viewModel.getInformation().observe(viewLifecycleOwner,object: Observer<UserInformation> {
            override fun onChanged(t: UserInformation?) {
                if(t != null) {
                    textView.text = t.name + " : " + t.zip
                }
            }
        })*/
        //let -> scope functions
        //the context object is available as an argument caled it
        //return value is a lambda result

        //?. -> safe call operatori
        //let only works with non null objects

        viewModel.getInformation().observe(viewLifecycleOwner, Observer {
            userInfo -> userInfo?.let {
                var myInfo = it
                textView.text = it.name + " : " + it.zip

                var str : StringRequest = StringRequest(Request.Method.GET,url + it.zip, Response.Listener<String> {
                    response ->
                    if(response == "{}") {

                    } else {
                        var zipResult = JSONObject(response);
                        var cityName = zipResult.getJSONArray("places").getJSONObject(0).getString("place name")
                        var stateName = zipResult.getJSONArray("places").getJSONObject(0).getString("state")
                        textView.text = myInfo.name + " : " + cityName + ", " + stateName
                    }
                },
                Response.ErrorListener {
                    textView.text = myInfo.name  + " : the zip code does not exist"
                })

                requestQueue.add(str)
            }
        })

        return view
    }

}