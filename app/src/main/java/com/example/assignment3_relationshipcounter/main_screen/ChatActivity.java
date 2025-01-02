package com.example.assignment3_relationshipcounter.main_screen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.ChatMessageList;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.firestore.Utils;
import com.example.assignment3_relationshipcounter.service.models.ChatRoom;
import com.example.assignment3_relationshipcounter.service.models.Message;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class ChatActivity extends AppCompatActivity {

    EditText chatBox;
    Button sendButton;
    ChatRoom chatRoom;
    String chatRoomID;
    String startUserId;
    String receiveUserId;
    DataUtils dataUtils = new DataUtils();
    ChatMessageList chatMessageList;
    RecyclerView chatRCV;
    Utils util = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatBox = findViewById(R.id.messBox);
        sendButton = findViewById(R.id.send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chatBox.getText().toString().isEmpty()){
                    sendMessage(chatBox.getText().toString());
                }
            }
        });

        /**
         * replace the startUserId with current login in user's ID
         * replace the receiverUserId with whatever user ID that you want to start the chat room
         */
        startUserId = "j0OXHJJ485axVcQyDclxKHOKKRe2";
        receiveUserId = "aP6X5YA1xfXPt61fxa6xayes0J12";
        chatRoomID = util.getChatRoomId(receiveUserId, startUserId);

        getOrCreateChatRoom();
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
    private void getOrCreateChatRoom(){
        System.out.println("Here0");
        dataUtils.getById("chatrooms", chatRoomID, ChatRoom.class, new DataUtils.FetchCallback<ChatRoom>() {
            @Override
            public void onSuccess(ChatRoom data) {
                System.out.println("Here1.5");
                chatRoom = data;
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("Here1");
                chatRoom = new ChatRoom(chatRoomID, Arrays.asList(startUserId, receiveUserId), Timestamp.now(), "");
                dataUtils.createNewChatRoom(chatRoomID, chatRoom, new DataUtils.NormalCallback<Void>() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Here2"+ chatRoomID);

                    }
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("ChatRoomID", "Cannot create chat room");
                        System.out.println("Here3");

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

        chatMessageList = new ChatMessageList(options, this, startUserId, receiveUserId);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chatRCV.setLayoutManager(manager);
        chatRCV.setAdapter(chatMessageList);
        chatMessageList.startListening();
        chatMessageList.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRCV.smoothScrollToPosition(0);
            }
        });
    }

}