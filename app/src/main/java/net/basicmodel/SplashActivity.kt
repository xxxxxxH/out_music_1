package net.basicmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.DataBase.OpenHelper
import net.general.GlobalApp
import java.util.*

class SplashActivity : AppCompatActivity() {
    var context: Context? = null
    var activity: Activity? = null
    private val PERMISSION_REQUEST_CODE = 1
    var storage_permission = -1
    var phone_state_permission: Int = -1
    var permissionsList: ArrayList<String>? = null
    var openHelper: OpenHelper? = null
    var is_finish = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_splash)
        context = this
        activity = this

        if (Build.VERSION.SDK_INT >= 23) {
            phone_state_permission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            storage_permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (phone_state_permission == PackageManager.PERMISSION_DENIED || storage_permission == PackageManager.PERMISSION_DENIED) {
                permissionWrapper()
            } else {
                Handler().postDelayed(
                    {
                        loadQueue()
                            .execute()
                    }, 100
                )
            }
        } else {
            Handler().postDelayed(
                {
                    loadQueue()
                        .execute()
                }, 100
            )
        }
    }

    private fun permissionWrapper() {
        val permissionsNeeded: MutableList<String> = ArrayList()
        permissionsList = ArrayList()
        if (!addPermission(
                permissionsList!!,
                Manifest.permission.READ_PHONE_STATE
            )
        ) permissionsNeeded.add("Read Phone State")
        if (!addPermission(
                permissionsList!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) permissionsNeeded.add("External Storage")
        if (permissionsList!!.size > 0) {
            if (permissionsNeeded.size > 0) {
                alert_dialog_warning()
                //  requestPermission();
                return
            }
            ActivityCompat.requestPermissions(
                activity!!,
                permissionsList!!.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
            return
        }
    }


    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context!!,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(permission)
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    permission
                )
            ) return false
        }
        return true
    }

    fun alert_dialog_warning() {
        is_finish = false
        var permissionAlert: AlertDialog.Builder? = null
        permissionAlert = AlertDialog.Builder(activity!!)
        permissionAlert.setTitle("Permission Alert")
        permissionAlert.setCancelable(false)
        permissionAlert.setMessage("To provide you proper service of application we need some permissions please allow the permission and enjoy playing songs.")
        permissionAlert.setPositiveButton(Html.fromHtml("<font color='black'>Ok</font>"),
            DialogInterface.OnClickListener { dialog, which ->
                requestPermission()
                dialog.cancel()
            })
        permissionAlert.setNegativeButton(Html.fromHtml("<font color='black'>Cancel</font>"),
            DialogInterface.OnClickListener { dialog, which -> activity!!.finish() })
        permissionAlert.show()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity!!,
            permissionsList!!.toTypedArray(),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                val perms: MutableMap<String, Int> = HashMap()
                // Initial
                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED

                // Fill with results
                var i = 0
                while (i < permissions.size) {
                    perms[permissions[i]] = grantResults[i]
                    i++
                }
                if (perms[Manifest.permission.READ_PHONE_STATE] != PackageManager.PERMISSION_GRANTED || perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] != PackageManager.PERMISSION_GRANTED) {
                    activity!!.finish()
                } else {
                    Handler().postDelayed(
                        { loadQueue().execute() }, 50
                    )
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun flakeImage() {
        GlobalApp.flakeArrayList.clear()
        GlobalApp.flakeArrayList.add(R.mipmap.transheartflake)
        GlobalApp.flakeArrayList.add(R.mipmap.staryellowflake)
        GlobalApp.flakeArrayList.add(R.mipmap.snowflake)
        GlobalApp.flakeArrayList.add(R.mipmap.flowerpinkflake)
        GlobalApp.flakeArrayList.add(R.mipmap.leafflake)
        GlobalApp.flakeArrayList.add(R.mipmap.starflake)
    }

    /* For Change main Background Image*/ /* start*/
    fun mainBackgroundImage() {
        GlobalApp.integerArrayList.clear()
        GlobalApp.integerArrayList.add(R.drawable.gradient1)
        GlobalApp.integerArrayList.add(R.drawable.gradient2)
        GlobalApp.integerArrayList.add(R.drawable.gradient3)
        GlobalApp.integerArrayList.add(R.drawable.gradient4)
        GlobalApp.integerArrayList.add(R.drawable.gradient5)
        GlobalApp.integerArrayList.add(R.drawable.gradient6)
        GlobalApp.integerArrayList.add(R.drawable.gradient7)
        GlobalApp.integerArrayList.add(R.drawable.gradient8)
        GlobalApp.integerArrayList.add(R.drawable.gradient9)
        GlobalApp.integerArrayList.add(R.drawable.gradient10)
        GlobalApp.integerArrayList.add(R.mipmap.bg1)
        GlobalApp.integerArrayList.add(R.mipmap.bg2)
        GlobalApp.integerArrayList.add(R.mipmap.bg3)
        GlobalApp.integerArrayList.add(R.mipmap.bg4)
        GlobalApp.integerArrayList.add(R.mipmap.bg5)
        GlobalApp.integerArrayList.add(R.mipmap.bg6)
        GlobalApp.integerArrayList.add(R.mipmap.bg7)
    }

    /* end*/
    fun smallBackgroundImage() {
        GlobalApp.integerArrayList_small.clear()
        GlobalApp.integerArrayList_small.add(R.drawable.gradient1)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient2)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient3)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient4)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient5)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient6)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient7)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient8)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient9)
        GlobalApp.integerArrayList_small.add(R.drawable.gradient10)
        GlobalApp.integerArrayList_small.add(R.mipmap.bgs1)
        GlobalApp.integerArrayList_small.add(R.mipmap.bgs2)
        GlobalApp.integerArrayList_small.add(R.mipmap.bgs3)
        GlobalApp.integerArrayList_small.add(R.mipmap.bgs4)
        GlobalApp.integerArrayList_small.add(R.mipmap.bgs5)
        GlobalApp.integerArrayList_small.add(R.mipmap.bgs6)
        GlobalApp.integerArrayList_small.add(R.mipmap.bgs7)
    }

    inner class loadQueue : AsyncTask<String?, Void?, String>() {

        override fun onPostExecute(result: String) {

            //  Intent intent = new Intent(context, SongsMainActivity.class);
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            // mainAct();
        }

        override fun onPreExecute() {}
        override fun doInBackground(vararg p0: String?): String {
            openHelper = OpenHelper(activity)
            try {
                smallBackgroundImage()
                mainBackgroundImage()
                flakeImage()
                GlobalApp.mediaItemsArrayList = openHelper!!.getQueueData(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "Executed"
        }
    }
}

