package test.zadanie.chatik

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.scaledrone.lib.*
import java.util.*
import kotlin.math.floor


class MainActivity : AppCompatActivity(), RoomListener {
    private val channelID = "oEUaUyo2db1rQxLd"
    private val roomName = "observable-room"
    private var editText: EditText? = null
    private var scaledrone: Scaledrone? = null
    private var messageAdapter: MessageAdapter? = null
    private var messagesView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // This is where we write the mesage
        editText = findViewById<View>(R.id.editText) as EditText

        messageAdapter = MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView?.adapter = messageAdapter;
        val data = MemberData(getRandomName(), getRandomColor())

        scaledrone = Scaledrone(channelID, data)
        scaledrone!!.connect(object : Listener {
            override fun onOpen() {
                println("Scaledrone connection open")
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone!!.subscribe(roomName, this@MainActivity)
            }

            override fun onOpenFailure(ex: java.lang.Exception?) {
                System.err.println(ex)
            }

            override fun onFailure(ex: java.lang.Exception?) {
                System.err.println(ex)
            }

            override fun onClosed(reason: String?) {
                System.err.println(reason)
            }
        })
    }

    // Successfully connected to Scaledrone room
    override fun onOpen(room: Room) {
        println("Conneted to room")
    }

    // Connecting to Scaledrone room failed
    override fun onOpenFailure(room: Room, ex: Exception) {
        System.err.println(ex)
    }

    override fun onMessage(room: Room?, receivedMessage: com.scaledrone.lib.Message) {
        val mapper = ObjectMapper()
        try {
            val data = mapper.treeToValue(receivedMessage.member.clientData, MemberData::class.java)
            val belongsToCurrentUser = receivedMessage.clientID == scaledrone!!.clientID
            val message = Message(receivedMessage.data.asText(), data, belongsToCurrentUser)
            runOnUiThread {
                    messageAdapter!!.add(message)
                    messagesView?.setSelection(messagesView!!.count - 1)
            }
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }

    private fun getRandomName(): String {
        val adjs = arrayOf("autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark", "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter", "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue", "billowing", "broken", "cold", "damp", "falling", "frosty", "green", "long", "late", "lingering", "bold", "little", "morning", "muddy", "old", "red", "rough", "still", "small", "sparkling", "throbbing", "shy", "wandering", "withered", "wild", "black", "young", "holy", "solitary", "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine", "polished", "ancient", "purple", "lively", "nameless")
        val nouns = arrayOf("waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning", "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter", "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook", "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly", "feather", "grass", "haze", "mountain", "night", "pond", "darkness", "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder", "violet", "water", "wildflower", "wave", "water", "resonance", "sun", "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper", "frog", "smoke", "star")
        return adjs[floor(Math.random() * adjs.size).toInt()] +
                "_" +
                nouns[floor(Math.random() * nouns.size).toInt()]
    }

    private fun getRandomColor(): String {
        val r = Random()
        val sb = StringBuffer("#")
        while (sb.length < 7) {
            sb.append(Integer.toHexString(r.nextInt()))
        }
        return sb.toString().substring(0, 7)
    }

    fun sendMessage(view: View) {
        val message = editText!!.text.toString()
        if (message.isNotEmpty()) {
            scaledrone!!.publish("observable-room", message)
            editText!!.text.clear()
        }
    }


}
