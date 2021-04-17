package com.app.basicfirebaseauth
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class UserAdapter(var mListUser:ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myView=LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
        return MyViewHolder(myView)
    }

    override fun getItemCount(): Int {
        return mListUser.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(mListUser[position])
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var nombres: TextView =itemView.findViewById(R.id.name)

        fun bindItem(mUser: User)
        {
            nombres.text=mUser.name
        }
    }
}