package horizonstudio.apps.pocktel.ui.fragments

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
import horizonstudio.apps.pocktel.configurations.Constants.HASH_ARGUMENT_NAME
import horizonstudio.apps.pocktel.configurations.Constants.HASH_COPY_CLIPBOARD_LABEL
import horizonstudio.apps.pocktel.configurations.Constants.RESULT_ARGUMENT_NAME
import horizonstudio.apps.pocktel.contracts.incoming.ScanResultContract
import horizonstudio.apps.pocktel.databinding.FragmentResultBinding
import horizonstudio.apps.pocktel.exceptions.PocktelInvalidArgumentsException
import horizonstudio.apps.pocktel.ui.adpters.MatchesListAdapter

// TODO: Organize layout
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

        hash = arguments?.getString(HASH_ARGUMENT_NAME)!!
        scanResult = arguments?.getParcelable<ScanResultContract>(RESULT_ARGUMENT_NAME)!!
        (activity as AppCompatActivity).supportActionBar?.title = hash
        binding.hash.text = hash

        binding.copyHashButton.setOnClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(HASH_COPY_CLIPBOARD_LABEL, hash)
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