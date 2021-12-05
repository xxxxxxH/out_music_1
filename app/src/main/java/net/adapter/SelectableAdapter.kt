package net.adapter

import android.util.SparseBooleanArray
import androidx.recyclerview.widget.RecyclerView
import net.event.MessageEvent
import net.fragment.SongsMainFragment
import org.greenrobot.eventbus.EventBus
import java.util.*

abstract class SelectableAdapter<VH : RecyclerView.ViewHolder?> :
    RecyclerView.Adapter<VH>() {
    private val selectedItems: SparseBooleanArray

    /**
     * Indicates if the item at position position is selected
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    fun isSelected(position: Int): Boolean {
        return getSelectedItems2().contains(position)
    }

    /**
     * Toggle the selection status of the item at a given position
     * @param position Position of the item to toggle the selection status for
     */
    fun toggleSelection(position: Int) {
        if (selectedItems[position, false]) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
//        SongsMainFragment.togggle()
        EventBus.getDefault().post(MessageEvent("togggle"))
        notifyItemChanged(position)
    }

    /**
     * Clear the selection status for all items
     */
    fun clearSelection() {
        try {
            val selection: List<Int> = getSelectedItems2()
            selectedItems.clear()
            for (i in selection) {
                notifyItemChanged(i)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Count the selected items
     * @return Selected items count
     */
    val selectedItemCount: Int
        get() = selectedItems.size()

    /**
     * Indicates the list of selected items
     * @return List of selected items ids
     */
    fun getSelectedItems(): Int {
        return selectedItems.size()
    }

    companion object {
        private val TAG = SelectableAdapter::class.java.simpleName
    }

    init {
        selectedItems = SparseBooleanArray()
    }

     fun getSelectedItems2(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }
}