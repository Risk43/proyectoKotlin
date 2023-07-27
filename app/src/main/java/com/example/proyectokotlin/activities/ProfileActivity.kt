package com.example.proyectokotlin.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.proyectokotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.properties.Delegates

class ProfileActivity : AppCompatActivity() {

    private lateinit var session: String

    private lateinit var lyProfileData: LinearLayout
    private lateinit var lyEditData: LinearLayout

    private lateinit var profile: TextView
    private lateinit var profileName: TextView
    private lateinit var profileLName: TextView
    private lateinit var profileEmail: TextView

    private lateinit var etEditName: EditText
    private lateinit var etEditLName: EditText

    private lateinit var ivBack: ImageView
    private lateinit var ivSingOut: ImageView
    private lateinit var ivProfilePhoto: ImageView

    private lateinit var btUpload: Button
    private lateinit var btEdit: Button
    private lateinit var btSave: Button
    private lateinit var btEditPhoto: ImageButton

    private lateinit var progressBar: ProgressBar

    private lateinit var storageReference: StorageReference
    private lateinit var rute_storage: String
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initVars()

        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider", provider)
        prefs.apply()

        val prefsRec = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        session = prefsRec.getString("email", null).toString()


        initComponents(session!!, ProviderType.EMAIL_PASSWORD)

    }

    private fun initVars(){
        lyProfileData = findViewById(R.id.lyProfileData)
        lyEditData = findViewById(R.id.lyEditData)

        profile = findViewById(R.id.tvProfile)
        profileName = findViewById(R.id.tvProfileName)
        profileLName = findViewById(R.id.tvProfileLName)
        profileEmail = findViewById(R.id.tvProfileEmail)

        etEditName = findViewById(R.id.etEditName)
        etEditLName = findViewById(R.id.etEditLName)

        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)

        btUpload = findViewById(R.id.btUpload)
        btEdit = findViewById(R.id.btEdit)
        btSave = findViewById(R.id.btSave)
        btEditPhoto = findViewById(R.id.btEditPhoto)

        progressBar = findViewById(R.id.progresBar)

        storageReference = FirebaseStorage.getInstance().reference

        lyEditData.visibility = View.INVISIBLE
        btUpload.visibility = View.INVISIBLE
    }
    private fun initComponents(session: String, provider: ProviderType){
        setData(session)
        goMain(session, provider)
        singOut()
        clickActions(session)
        updatePhoto(session)
    }

    private fun singOut(){
        ivSingOut = findViewById(R.id.ivSingOut)

        ivSingOut.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun  setData(session: String){
        if (session != null){
            FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { result ->
                for (document in result){
                    if (document.getString("email") == session){
                        val name = document.getString("name")
                        val lName = document.getString("lastName")
                        val email = document.getString("email")
                        val photo = document.getString("photo")

                        profile.text = name
                        profileName.text = name
                        profileLName.text = lName
                        profileEmail.text = email
                        Glide.with(this).load(photo).into(ivProfilePhoto)
                    }
                }
            }
        }
    }

    private fun clickActions(session: String){
        btEditPhoto.setOnClickListener {
            resultLauncher.launch("image/*")
            btUpload.visibility = View.VISIBLE
        }
        btUpload.setOnClickListener {
            selectPhoto()
            btUpload.visibility = View.INVISIBLE
        }
        btEdit.setOnClickListener {
            lyProfileData.visibility = View.INVISIBLE
            lyEditData.visibility = View.VISIBLE
            btEdit.visibility = View.INVISIBLE

        }
        btSave.setOnClickListener {
            updateData()
            setData(session)
            lyProfileData.visibility = View.VISIBLE
            lyEditData.visibility = View.INVISIBLE
            btEdit.visibility = View.VISIBLE
        }
    }


    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){
            imageUri = it
            ivProfilePhoto.setBackgroundResource(R.color.transparent)
            ivProfilePhoto.setImageURI(imageUri)
    }


    private fun selectPhoto(){
        progressBar.visibility = View.VISIBLE
        rute_storage = "users/" + session
        val reference = storageReference.child(rute_storage)
        reference.putFile(imageUri).addOnCompleteListener { task->
            if (task.isSuccessful){
                reference.downloadUrl.addOnSuccessListener { uri->
                    val downloadUri = uri.toString()
                    val map = hashMapOf<String, Any>()
                    map.put("photo",downloadUri)

                       FirebaseFirestore.getInstance().collection("users").document(session).update(map).addOnCompleteListener{ firestoreTask->
                           if (firestoreTask.isSuccessful){
                               Toast.makeText(this,"Foto actualizada", Toast.LENGTH_SHORT).show()
                           }else{
                               Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                           }
                           progressBar.visibility = View.GONE
                       }
                }
            }else{
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePhoto(session: String){
        if (session != null){
            FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { result ->
                for (document in result){
                    if (document.getString("email") == session){
                        val photo = document.getString("photo")

                        Glide.with(this).load(photo).into(ivProfilePhoto)
                    }
                }
            }
        }
    }

    private fun updateData(){
        val name = etEditName.text.toString()
        val lName = etEditLName.text.toString()

        progressBar.visibility = View.VISIBLE

        if (name.isEmpty() || lName.isEmpty()){
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
        }else{
            val map = hashMapOf<String, Any>()
            map.put("name", name)
            map.put("lastName", lName)
            FirebaseFirestore.getInstance().collection("users").document(session).update(map).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Información actualizada", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
                etEditName.text = null
                etEditLName.text = null
            }
        }

    }

    private fun goMain(session: String, provider: ProviderType) {
        ivBack = findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", session)
            intent.putExtra("provider", provider.name)
            startActivity(intent)
        }
    }
}