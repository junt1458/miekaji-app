package jp.ac.gifu_nct.miekaji.ui.friend

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import jp.ac.gifu_nct.miekaji.R

class GroupAdapter(private val friend:List<FriendData>, private val listener: ListListener):
    RecyclerView.Adapter<FriendHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        Log.d("Adapter","onCreateViewHolder")
        val rowView:View=LayoutInflater.from(parent.context).inflate(R.layout.recycle_item,parent,false)
        return FriendHolder(rowView)
    }

    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        Log.d("Adapter","onBindViewHolder")
        holder.nameView.text=friend[position].name
        holder.toGroup.setVisibility(View.INVISIBLE)
        holder.itemView.setOnClickListener {
            listener.onClickRow(it,friend[position])
        }
    }

    override fun getItemCount():Int{
        Log.d("Adapter","getItemCount")
        return friend.size
    }

    interface ListListener{
        fun onClickRow(tappedView: View, friendData: FriendData)
    }
}