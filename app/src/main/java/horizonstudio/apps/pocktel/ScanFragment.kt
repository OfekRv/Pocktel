package horizonstudio.apps.pocktel

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import horizonstudio.apps.pocktel.bl.RuleSetBl
import horizonstudio.apps.pocktel.bl.scanners.FileScanner
import horizonstudio.apps.pocktel.configurations.Constants.ALL_FILES_PATTERN
import horizonstudio.apps.pocktel.configurations.Constants.ARCHIVED_FILES_PATTERN
import horizonstudio.apps.pocktel.configurations.Constants.DATABASE_NAME
import horizonstudio.apps.pocktel.configurations.Constants.HASH_ARGUMENT_NAME
import horizonstudio.apps.pocktel.configurations.Constants.NOT_YET_ASSIGNED_ID
import horizonstudio.apps.pocktel.configurations.Constants.RESULT_ARGUMENT_NAME
import horizonstudio.apps.pocktel.contracts.incoming.ScanResultContract
import horizonstudio.apps.pocktel.dal.PocktelDatabase
import horizonstudio.apps.pocktel.dal.entities.RuleSet
import horizonstudio.apps.pocktel.databinding.FragmentScanBinding
import horizonstudio.apps.pocktel.exceptions.PocktelInvalidArgumentsException
import horizonstudio.apps.pocktel.ui.adpters.RuleSetListAdapter
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.downloadFile
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.getFileName
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.saveTempFile
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.sha256
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URL


class ScanFragment : Fragment() {
    private var scanner: FileScanner = FileScanner()
    private val ruleSetBl: RuleSetBl by lazy {
        RuleSetBl(
            Room.databaseBuilder(requireContext(), PocktelDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries().build().ruleSetRepository()
        )
    }

    private var _binding: FragmentScanBinding? = null

    private var sampleFile: File? = null
    private var ruleSetFile: File? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = view.findViewById(R.id.rule_set_spinner) as Spinner
        spinner.adapter = RuleSetListAdapter(requireContext(), ruleSetBl.findAll())

        val chooseSample = registerForActivityResult(OpenDocument()) { uri: Uri? ->
            uri?.let {
                val name = getFileName(uri, requireContext())
                sampleFile = saveTempFile(uri, name, requireContext())
                binding.sampleFileName.setText(name)
            }
        }

        val chooseRuleSet = registerForActivityResult(OpenDocument()) { uri: Uri? ->
            uri?.let {
                val name = getFileName(uri, requireContext())
                ruleSetFile = saveTempFile(uri, name, requireContext())
                ruleSetBl.save(
                    RuleSet(
                        NOT_YET_ASSIGNED_ID,
                        ruleSetFile!!.nameWithoutExtension,
                        ruleSetFile!!.path,
                        null
                    )
                )
                spinner.adapter = RuleSetListAdapter(requireContext(), ruleSetBl.findAll())
                binding.ruleSetFileName.setText(name)
            }
        }

        binding.chooseFileButton.setOnClickListener {
            chooseSample.launch(arrayOf(ALL_FILES_PATTERN))
        }

        binding.chooseRuleFileButton.setOnClickListener {
            chooseRuleSet.launch(arrayOf(ARCHIVED_FILES_PATTERN))
        }

        binding.scanButton.setOnClickListener {
            if (sampleFile == null || ruleSetFile == null) {
                throw PocktelInvalidArgumentsException("Please choose sample file and rules to scan")
            }

            val hash = sha256(sampleFile!!)
            var result: ScanResultContract
            runBlocking { result = scanner.scan(sampleFile!!, ruleSetFile!!) }
            val args = Bundle()
            args.putString(HASH_ARGUMENT_NAME, hash)
            args.putParcelable(RESULT_ARGUMENT_NAME, result)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, args)
        }

        val popupButton = requireView().findViewById<Button>(R.id.chooseRuleUrlButton)
        popupButton.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.choose_rules_url)

            val inputText = dialog.findViewById<EditText>(R.id.input_text)
            val okButton = dialog.findViewById<Button>(R.id.ok_button)

            okButton.setOnClickListener {
                val plainUrl = inputText.text.toString()
                binding.ruleSetFileName.setText(plainUrl)
                Thread {
                    dialog.dismiss()
                    ruleSetFile = downloadFile(URL(plainUrl))
                    ruleSetBl.save(
                        RuleSet(
                            NOT_YET_ASSIGNED_ID, ruleSetFile!!.nameWithoutExtension, null, plainUrl
                        )
                    )
                    spinner.adapter = RuleSetListAdapter(requireContext(), ruleSetBl.findAll())

                }.start()
            }

            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}