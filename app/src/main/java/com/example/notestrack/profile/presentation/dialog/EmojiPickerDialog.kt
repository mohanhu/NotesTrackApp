package com.example.notestrack.profile.presentation.dialog

import android.content.Context
import android.os.Bundle
import com.example.notestrack.databinding.MoreEmojiDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class EmojiPickerDialog(
    context: Context,
    private val pickedEmoji:(String)->Unit
) :BottomSheetDialog(context){

    private val binding: MoreEmojiDialogBinding by lazy { MoreEmojiDialogBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.emojiPickerView.setOnEmojiPickedListener{
            pickedEmoji.invoke(it.emoji)
            dismiss()
        }

        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }
}