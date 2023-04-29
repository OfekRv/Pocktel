package horizonstudio.apps.pocktel

import android.content.ClipData
import android.content.Context
import android.content.ClipboardManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import horizonstudio.apps.pocktel.contracts.incoming.ScanResultContract
import horizonstudio.apps.pocktel.databinding.FragmentResultBinding
import horizonstudio.apps.pocktel.exceptions.PocktelInvalidArgumentsException
import horizonstudio.apps.pocktel.ui.adpters.MatchesListAdapter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ResultFragment : Fragment() {
    private lateinit var hash: String
    private lateinit var scanResult: ScanResultContract

    private var _binding: FragmentResultBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (arguments == null) {
            throw PocktelInvalidArgumentsException("No arguments received")
        }

        hash = arguments?.getString("hash")!!
        scanResult = arguments?.getParcelable<ScanResultContract>("result")!!
        (activity as AppCompatActivity).supportActionBar?.title = hash
        binding.hash.text = hash

        binding.copyHashButton.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("hash", hash)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Hash copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        binding.matchesList.adapter = MatchesListAdapter(requireContext(), scanResult.matches.toList())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}