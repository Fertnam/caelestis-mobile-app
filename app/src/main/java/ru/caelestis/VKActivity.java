package ru.caelestis;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import ru.caelestis.restapi.Image;

public class VKActivity extends AppCompatActivity {
    private ListView friendsList;

    private TextView currentUserTextView;

    private ImageView vkAvatar;

    private ArrayList<String> friends = new ArrayList(),
                              friendsAvatartsUrl = new ArrayList();

    private ArrayList<Bitmap> friendsAvatarsBitmap = new ArrayList();

    private VKFriendsAdapter adapter = new VKFriendsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk);

        friendsList = (ListView) findViewById(R.id.friendsList);
        currentUserTextView = (TextView) findViewById(R.id.currentUserTextView);
        vkAvatar = (ImageView) findViewById(R.id.vkAvatar);

        VK.login(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        VKAuthCallback callback = new VKAuthCallback() {
            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                fillCurrentUser();
                fillCurrentUserAvatar();
                fillFriends();
            }

            @Override
            public void onLoginFailed(int i) {

            }

            private void fillCurrentUser() {
                VKRequest request = new VKRequest("account.getProfileInfo");

                VK.execute(request, new VKApiCallback() {
                    @Override
                    public void success(Object o) {
                        try {
                            JSONObject json = new JSONObject(o.toString()).getJSONObject("response");
                            currentUserTextView.setText(json.getString("first_name") + " " + json.getString("last_name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void fail(@NotNull Exception e) {

                    }
                });
            }

            private void fillCurrentUserAvatar() {
                VKRequest request = new VKRequest("users.get");

                request.addParam("fields", "photo_200_orig");

                VK.execute(request, new VKApiCallback() {
                    @Override
                    public void success(Object o) {
                        try {
                            JSONObject json = new JSONObject(o.toString()).getJSONArray("response").getJSONObject(0);

                            String avatarUrl = json.getString("photo_200_orig");
                            new AvatarDownloadAsyncTask(1, avatarUrl).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void fail(@NotNull Exception e) {

                    }
                });
            }

            private void fillFriends() {
                VKRequest request = new VKRequest("friends.get");
                request.addParam("fields", "nickname,photo_200_orig");

                VK.execute(request, new VKApiCallback() {
                    @Override
                    public void success(Object o) {
                        try {
                            JSONObject json = new JSONObject(o.toString()).getJSONObject("response");
                            JSONArray jsonArray = json.getJSONArray("items");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject friend = jsonArray.getJSONObject(i);

                                friends.add(friend.getString("first_name") + " " + friend.getString("last_name"));

                                new AvatarDownloadAsyncTask(2, friend.getString("photo_200_orig")).execute();
                            }

                            friendsList.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void fail(@NotNull Exception e) {

                    }
                });
            }
        };

        if (VK.onActivityResult(requestCode, resultCode, data, callback)) {

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * AsyncTask для загрузки скинов с сервера
     * @autor Миколенко Евгений (Fertnam)
     * @version 2
     */
    class AvatarDownloadAsyncTask extends Image {
        private int mode;

        /**
         * Конструктор, в котором создаётся AsyncTask
         * @param mode - режим загрузки скина
         */
        public AvatarDownloadAsyncTask(final int mode, final String url) {
            super();

            this.mode = mode;
            this.url = url;
        }

        /**
         * Метод, в котором подставляются скины в ImageView
         * @param imageBitmap - пиксельное представления скина
         */
        @Override
        protected void onPostExecute(Bitmap imageBitmap) {
            if (mode == 1) {
                vkAvatar.setImageBitmap(imageBitmap);
            } else {
                friendsAvatarsBitmap.add(imageBitmap);
                adapter.notifyDataSetChanged();
            }
        }
    }

    class VKFriendsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return friends.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.vk_list_item, null);

            try {
                ImageView imageView = view.findViewById(R.id.friendsAvatar);
                TextView textView = view.findViewById(R.id.friendsName);

                imageView.setImageBitmap(friendsAvatarsBitmap.get(position));
                textView.setText(friends.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
}
