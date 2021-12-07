package net.basicmodel

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.android.synthetic.main.layout_activity_video.*

class VideoPlayerActivity:AppCompatActivity() {
    var orientationUtils: OrientationUtils? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_video)
        initData()
    }

    private fun initData() {
        val i = intent
        val url = i.getStringExtra("url") as String
        val name = i.getStringExtra("name") as String
        initVideoPlayer(url,name)
    }

    private fun initVideoPlayer(url: String, name: String){
        videoPlayer.setUp(url, true, name)
        //增加title
        videoPlayer.titleTextView.visibility = View.VISIBLE;
        //设置返回键
        videoPlayer.backButton.visibility = View.VISIBLE;
        orientationUtils = OrientationUtils(this, videoPlayer)
        videoPlayer.fullscreenButton.setOnClickListener {
            orientationUtils!!.resolveByClick()
        }
        videoPlayer.backButton.setOnClickListener {
            onBackPressed()
        }
        videoPlayer.startPlayLogic()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        orientationUtils?.releaseListener()
    }

    override fun onBackPressed() {
        //先返回正常状态
        if (orientationUtils!!.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.fullscreenButton.performClick()
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed()
    }

}