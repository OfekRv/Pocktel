package horizonstudio.apps.pocktel.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import horizonstudio.apps.pocktel.R
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
import horizonstudio.apps.pocktel.exceptions.PocktelException
import horizonstudio.apps.pocktel.exceptions.PocktelInvalidArgumentsException
import horizonstudio.apps.pocktel.ui.adpters.RuleSetListAdapter
import horizonstudio.apps.pocktel.ui.dialogs.ErrorDialog.Companion.errorDialog
import horizonstudio.apps.pocktel.ui.dialogs.RuleSetDialog
import horizonstudio.apps.pocktel.utils.DatabaseUtil.Companion.buildDatabase
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.downloadFile
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.getFileName
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.saveTempFile
import horizonstudio.apps.pocktel.utils.FileUtil.Companion.sha256
import kotlinx.coroutines.*
import java.io.File
import java.net.URL

// TODO: figure out why loading doesnt shows, change animation, find more places for it
// TODO: DB seed
// TODO: beautify layout
// TODO: PocktelException handling (wrong host)
// TODO: work with coroutine (db?)
class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var loadingView: ImageView
    private lateinit var spinner: Spinner

    private var sampleFile: File? = null
    private var ruleSetFile: File? = null

    private var scanner: FileScanner = FileScanner()
    private val ruleSetBl: RuleSetBl by lazy {
        RuleSetBl(
            buildDatabase<PocktelDatabase>(
                requireContext(), DATABASE_NAME
            ).ruleSetRepository()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = view.findViewById(R.id.loading_view)

        spinner = view.findViewById(R.id.rule_set_spinner) as Spinner
        spinner.adapter = RuleSetListAdapter(requireContext(), ruleSetBl.findAll())

        val chooseSample = registerForActivityResult(OpenDocument()) { uri: Uri? ->
            uri?.let {
                val name = getFileName(uri, requireContext())
                sampleFile = saveTempFile(uri, name, requireContext())
                binding.sampleFileName.setText(name)
            }
        }

        val chooseRuleSetName = registerForActivityResult(OpenDocument()) { uri: Uri? ->
            uri?.let {
                val fileName = getFileName(uri, requireContext())
                RuleSetDialog.chooseRuleSetNameDialog(
                    requireContext(), saveRuleSet(uri, fileName)
                )
            }
        }

        val chooseRuleSetUrl = requireView().findViewById<Button>(R.id.chooseRuleUrlButton)
        chooseRuleSetUrl.setOnClickListener {
            RuleSetDialog.chooseRuleSetUrlDialog(
                requireContext(), downloadRuleSet()
            )
        }

        binding.scanButton.setOnClickListener {
            scan()
        }

        binding.chooseFileButton.setOnClickListener {
            chooseSample.launch(arrayOf(ALL_FILES_PATTERN))
        }

        binding.chooseRuleFileButton.setOnClickListener {
            chooseRuleSetName.launch(arrayOf(ARCHIVED_FILES_PATTERN))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun scan() {
        try {
            validateScanInputs()
        } catch (e: PocktelException) {
            errorDialog(requireContext(), e.message!!)
            return
        }

        loadingView.visibility = View.VISIBLE
        uiScope.launch {
            ruleSetFile = prepareRuleSetFile(binding.ruleSetSpinner.selectedItem as RuleSet)
            val hash = sha256(sampleFile!!)
            val result: ScanResultContract
            try {
                result = scanner.scan(sampleFile!!, ruleSetFile!!)
                transferToResultFragment(hash, result)
            } catch (e: PocktelException) {
                errorDialog(requireContext(), e.message!!)
            }
        }
        loadingView.visibility = View.GONE
    }

    private fun downloadRuleSet(): OnClickListener {
        return OnClickListener {
            val params = it.tag as HashMap<String, String>
            // TODO: validate inputs
            loadingView.visibility = View.VISIBLE
            // TODO: consts
            val name: String = params["name"]!!
            val plainUrl: String = params["url"]!!

            uiScope.launch {
                withContext(Dispatchers.IO) {
                    ruleSetFile = downloadFile(requireContext(), URL(plainUrl))
                    val ruleId = ruleSetBl.save(
                        RuleSet(NOT_YET_ASSIGNED_ID, name, null, plainUrl)
                    )

                    updateRuleSetSpinner(ruleId)
                }
            }
            loadingView.visibility = View.GONE
        }
    }

    private suspend fun updateRuleSetSpinner(selectedRuleId: Long) {
        val rules = ruleSetBl.findAll()
        val ruleIndex = rules.indexOfFirst { ruleSet -> ruleSet.id == selectedRuleId }

        withContext(Dispatchers.Main) {
            spinner.adapter = RuleSetListAdapter(requireContext(), rules)
            spinner.setSelection(ruleIndex)
        }
    }

    private fun saveRuleSet(uri: Uri, fileName: String): OnClickListener {
        return OnClickListener { view ->
            // TODO: validate inputs
            loadingView.visibility = View.VISIBLE
            val ruleSetName = view.tag as String
            uiScope.launch {
                ruleSetFile = saveTempFile(uri, fileName, requireContext())
                val ruleId = ruleSetBl.save(
                    RuleSet(
                        NOT_YET_ASSIGNED_ID, ruleSetName, ruleSetFile!!.path, null
                    )
                )
                updateRuleSetSpinner(ruleId)
            }
            loadingView.visibility = View.GONE
        }
    }

    private fun transferToResultFragment(hash: String, result: ScanResultContract) {
        val args = Bundle()
        args.putString(HASH_ARGUMENT_NAME, hash)
        args.putParcelable(RESULT_ARGUMENT_NAME, result)
        findNavController().navigate(R.id.action_ScanFragment_to_ResultFragment, args)
    }

    private suspend fun prepareRuleSetFile(ruleSet: RuleSet): File {
        ruleSet.path?.let { return File(ruleSet.path) }
        ruleSet.url?.let {
            return withContext(Dispatchers.IO) {
                return@withContext downloadFile(
                    requireContext(), URL(
                        ruleSet.url
                    )
                )
            }
        }
        throw PocktelInvalidArgumentsException("Could not locate either path nor url of rule set")
    }

    private fun validateScanInputs() {
        if (sampleFile == null) {
            throw PocktelInvalidArgumentsException("Please choose sample file to scan")
        }

        if (binding.ruleSetSpinner.isEmpty()) {
            throw PocktelInvalidArgumentsException("Please choose rule set")
        }
    }
}