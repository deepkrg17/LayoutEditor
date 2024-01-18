package com.itsvks.layouteditor.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClipboardUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itsvks.layouteditor.ProjectFile
import com.itsvks.layouteditor.R
import com.itsvks.layouteditor.adapters.models.ValuesItem
import com.itsvks.layouteditor.databinding.LayoutValuesItemBinding
import com.itsvks.layouteditor.databinding.LayoutValuesItemDialogBinding
import com.itsvks.layouteditor.tools.ColorPickerDialogFlag
import com.itsvks.layouteditor.utils.BitmapUtil.getLuminance
import com.itsvks.layouteditor.utils.BitmapUtil.setImageTintAccordingToBackground
import com.itsvks.layouteditor.utils.BitmapUtil.setTextColorAccordingToBackground
import com.itsvks.layouteditor.utils.FileUtil
import com.itsvks.layouteditor.utils.NameErrorChecker
import com.itsvks.layouteditor.utils.SBUtils
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class ColorResourceAdapter(
  private val project: ProjectFile,
  private val colorList: MutableList<ValuesItem>
) : RecyclerView.Adapter<ColorResourceAdapter.VH>() {
  class VH(var binding: LayoutValuesItemBinding) : RecyclerView.ViewHolder(
    binding.getRoot()
  ) {
    val colorName: TextView = binding.name
    val colorValue: TextView = binding.value
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(
      LayoutValuesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.colorName.text = colorList[position].name
    holder.colorValue.text = colorList[position].value
    holder.binding.root.animation = AnimationUtils.loadAnimation(
      holder.itemView.context, R.anim.project_list_animation
    )
    holder.binding
      .root
      .setBackgroundColor(Color.parseColor(colorList[position].value))
    setTextColorAccordingToBackground(holder.binding.getRoot(), holder.colorName)
    setImageTintAccordingToBackground(holder.binding.menu, holder.binding.getRoot())
    if (getLuminance(holder.binding.getRoot()) >= 0.5) {
      holder.colorValue.setTextColor(Color.parseColor("#FF313131"))
      holder.binding.versions.setTextColor(Color.parseColor("#FF313131"))
    } else {
      holder.colorValue.setTextColor(Color.parseColor("#FFD9D9D9"))
      holder.binding.versions.setTextColor(Color.parseColor("#FFD9D9D9"))
    }
    holder.binding.menu.setOnClickListener { showOptions(it, position) }
    holder.binding
      .getRoot()
      .setOnClickListener { editColor(it, position) }
  }

  override fun getItemCount(): Int {
    return colorList.size
  }

  fun generateColorsXml() {
    val colorsPath = project.colorsPath
    val sb = StringBuilder()
    sb.append("<resources>\n")
    for (colorItem in colorList) {
      // Generate color item code
      sb.append("\t<color name=\"")
        .append(colorItem.name)
        .append("\">")
        .append(colorItem.value)
        .append("</color>\n")
    }
    sb.append("</resources>")
    FileUtil.writeFile(colorsPath, sb.toString().trim { it <= ' ' })
  }

  @SuppressLint("NotifyDataSetChanged")
  private fun showOptions(v: View, position: Int) {
    val popupMenu = PopupMenu(v.context, v)
    popupMenu.inflate(R.menu.menu_values)
    popupMenu.setOnMenuItemClickListener { item: MenuItem ->
      val id = item.itemId
      if (id == R.id.menu_copy_name) {
        ClipboardUtils.copyText(colorList[position].name)
        SBUtils.make(
          v,
          v.context.getString(R.string.copied)
            + " "
            + colorList[position].name
        )
          .setSlideAnimation()
          .showAsSuccess()
        return@setOnMenuItemClickListener true
      } else if (id == R.id.menu_delete) {
        MaterialAlertDialogBuilder(v.context)
          .setTitle("Remove Color")
          .setMessage("Do you want to remove " + colorList[position].name + "?")
          .setNegativeButton(R.string.no, null)
          .setPositiveButton(
            R.string.yes
          ) { _, _ ->
            val name = colorList[position].name
            if (name == "default_color") {
              SBUtils.make(
                v,
                v.context
                  .getString(
                    R.string.msg_cannot_delete_default, "color"
                  )
              )
                .setFadeAnimation()
                .setType(SBUtils.Type.INFO)
                .show()
            } else {
              colorList.removeAt(position)
              notifyDataSetChanged()
              generateColorsXml()
            }
          }
          .show()
        return@setOnMenuItemClickListener true
      }
      false
    }
    popupMenu.show()
  }

  @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
  private fun editColor(v: View, pos: Int) {
    val builder = MaterialAlertDialogBuilder(v.context)
    builder.setTitle("Edit Color")
    val bind = LayoutValuesItemDialogBinding.inflate(builder.create().layoutInflater)
    val ilName = bind.textInputLayoutName
    val etName = bind.textinputName
    val etValue = bind.textinputValue
    etName.setText(colorList[pos].name)
    etValue.setText(colorList[pos].value)
    etValue.isFocusable = false
    builder.setView(bind.getRoot())
    etValue.setOnClickListener {
      val dialog: ColorPickerDialog.Builder = ColorPickerDialog.Builder(v.context)
        .setTitle("Choose Color")
        .setPositiveButton(v.context.getString(R.string.confirm),
          ColorEnvelopeListener { envelope, _ ->
            etValue.setText("#${envelope.hexCode}")
          }
        )
        .setNegativeButton(v.context.getString(R.string.cancel)) { d, _ -> d.dismiss() }
        .attachAlphaSlideBar(true)
        .attachBrightnessSlideBar(true)
        .setBottomSpace(12)

      val colorView = dialog.colorPickerView
      colorView.setFlagView(ColorPickerDialogFlag(v.context))
      colorView.setInitialColor(Color.parseColor(colorList[pos].value))
      dialog.show()
    }
    builder.setPositiveButton(
      R.string.okay
    ) { _, _ ->
      if (colorList[pos].name == "default_color" && etName.getText()
          .toString() != "default_color"
      ) {
        SBUtils.make(v, v.context.getString(R.string.msg_cannot_rename_default, "color"))
          .setFadeAnimation()
          .setType(SBUtils.Type.INFO)
          .show()
      } else {
        // Update position
        colorList[pos].name = etName.getText().toString()
      }
      // Update position
      colorList[pos].value = etValue.getText().toString()
      notifyDataSetChanged()
      // Generate code from all colors in list
      generateColorsXml()
    }
    builder.setNegativeButton(R.string.cancel, null)
    val dialog = builder.create()
    dialog.show()
    etName.addTextChangedListener(
      object : TextWatcher {
        override fun beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}
        override fun onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}
        override fun afterTextChanged(p1: Editable) {
          NameErrorChecker.checkForValues(
            etName.getText().toString(), ilName, dialog, colorList, pos
          )
        }
      })
    NameErrorChecker.checkForValues(etName.getText().toString(), ilName, dialog, colorList, pos)
  }
}
