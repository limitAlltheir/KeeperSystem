package by.limitalltheir.keepersystem.interfaces

import android.view.View

interface OnItemClick {

    fun onItemClick(position: Int)
    fun onItemClick(position: Int, id: View)
}