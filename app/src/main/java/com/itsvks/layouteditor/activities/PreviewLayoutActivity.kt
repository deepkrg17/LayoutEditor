package com.itsvks.layouteditor.activities

import android.os.Bundle
import com.itsvks.layouteditor.BaseActivity
import com.itsvks.layouteditor.LayoutFile
import com.itsvks.layouteditor.ProjectFile
import com.itsvks.layouteditor.databinding.ActivityPreviewLayoutBinding
import com.itsvks.layouteditor.tools.XmlLayoutParser
import com.itsvks.layouteditor.utils.Constants

class PreviewLayoutActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityPreviewLayoutBinding.inflate(
      layoutInflater
    )
    setContentView(binding.getRoot())
    val projectFile = intent.extras!!.getParcelable<ProjectFile>(Constants.EXTRA_KEY_PROJECT)
    val parser = XmlLayoutParser(this)
//    parser.parseFromXml(projectFile!!.layoutPath, this)
    binding.getRoot().addView(parser.getRoot())
  }

  companion object {
    const val EXTRA_KEY_XML = "xml"
  }
}
