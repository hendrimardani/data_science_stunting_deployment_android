import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.Database.LayananKeluarga.LayananKeluargaEntity
import com.example.stunting.Database.RemajaPutri.RemajaPutriEntity
import com.example.stunting.R
import com.example.stunting.databinding.ItemAllAdapterBinding

class LayananKeluargaAdapter(val items: ArrayList<LayananKeluargaEntity>) : RecyclerView.Adapter<LayananKeluargaAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemAllAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val llItem = binding.llItem
        val tgl = binding.tvItemTanggal
        val namaAyah = binding.tvItemNik
        val namaIbu = binding.tvItemNama
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAllAdapterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tgl.text = item.tanggal
        holder.namaAyah.text = item.namaLayananKeluarga
        holder.namaIbu.text = item.namaLengkapIbuHamilLayananKeluarga

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(
                ContextCompat
                .getColor(holder.itemView.context, R.color.light_gray))
        } else {
            holder.llItem.setBackgroundColor(
                ContextCompat
                .getColor(holder.itemView.context, R.color.white))
        }
    }
}