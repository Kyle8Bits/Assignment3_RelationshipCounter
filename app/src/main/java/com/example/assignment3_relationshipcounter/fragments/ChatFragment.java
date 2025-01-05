package com.example.assignment3_relationshipcounter.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.ChatList;

import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.ChatRoom;
import com.example.assignment3_relationshipcounter.service.models.Message;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class ChatFragment extends AppCompatActivity {

    EditText chatBox;
    Button sendButton;
    ChatRoom chatRoom;
    String chatRoomID;
    String startUserId;
    String receiveUserId;
    DataUtils dataUtils = new DataUtils();
    ChatList chatList;
    RecyclerView chatRCV;
    TextView texterName;
    Utils util = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        User user = (User) getIntent().getSerializableExtra("otherUser");
        texterName= findViewById(R.id.texter_name);
        texterName.setText(user.getUsername());
        chatBox = findViewById(R.id.messBox);
        sendButton = findViewById(R.id.send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chatBox.getText().toString().isEmpty()){
                   getOrCreateChatRoom(new CreateRoomCallback() {
                       @Override
                       public void onSuccess() {
                           sendMessage(chatBox.getText().toString());
                       }

                       @Override
                       public void onFailure(Exception e) {

                       }
                   });
                }
            }
        });

        /**
         * replace the startUserId with current login in user's ID
         * replace the receiverUserId with whatever user ID that you want to start the chat room
         */
        startUserId = new Authentication().getFUser().getUid();
        receiveUserId = user.getId();
        chatRoomID = util.getChatRoomId(receiveUserId, startUserId);

        setUpChatHistory();
    }

    private void sendMessage(String messagePreview) {
        chatRoom.setLastMessageTime(Timestamp.now());
        chatRoom.setLastMessageSenderId(startUserId);
        chatRoom.setLastMessage(messagePreview);
        dataUtils.updateById("chatrooms", chatRoomID, chatRoom, new DataUtils.NormalCallback<ChatRoom>() {
            @Override
            public void onSuccess() {
                Message message = new Message(messagePreview, startUserId, Timestamp.now());
                dataUtils.addNewCollectionToDocument("chatrooms", chatRoomID, "chats", message, new DataUtils.NormalCallback<Message>() {
                    @Override
                    public void onSuccess() {
                        chatBox.setText("");
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("ChatRoomID", "Cannot add message");
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                Log.w("ChatRoomID", "Cannot update chat room");
            }
        });
    }
    private void getOrCreateChatRoom(CreateRoomCallback callback){
        dataUtils.getById("chatrooms", chatRoomID, ChatRoom.class, new DataUtils.FetchCallback<ChatRoom>() {
            @Override
            public void onSuccess(ChatRoom data) {
                chatRoom = data;
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                chatRoom = new ChatRoom(chatRoomID, Arrays.asList(startUserId, receiveUserId), Timestamp.now(), "");
                dataUtils.createNewChatRoom(chatRoomID, chatRoom, new DataUtils.NormalCallback<Void>() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("ChatRoomID", "Cannot create chat room");

                    }
                });
            }
        });
    }
    private void setUpChatHistory(){
        chatRCV = findViewById(R.id.chatRoomRCV);
        Query query = dataUtils.getChatInChatroom(chatRoomID);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();

        chatList = new ChatList(options, this, startUserId, receiveUserId);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chatRCV.setLayoutManager(manager);
        chatRCV.setAdapter(chatList);
        chatList.startListening();
        chatList.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRCV.smoothScrollToPosition(0);
            }
        });
    }

    interface CreateRoomCallback{
        void onSuccess();
        void onFailure(Exception e);

    }

}