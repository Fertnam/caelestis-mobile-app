package ru.caelestis;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.requests.VKRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import ru.caelestis.restapi.Image;
import ru.caelestis.enums.VkAvatar;

/**
 * Activity для вывлода данных аккаунта ВК
 * @autor Миколенко Евгений (Fertnam)
 * @version 2
 */
public class VKActivity extends AppCompatActivity {
    /**
     * Поле, хранящее объект класса ListView для хранения списка друзей
     */
    private ListView friendsList;

    /**
     * Поле, хранящее объект класса TextView для отображения имени текущего пользователя
     */
    private TextView currentUserTextView;

    /**
     * Поле, хранящее объект класса ImageView для отображения аватарки текущего пользователя
     */
    private ImageView vkCurrentUserAvatar;

    /**
     * Поле, хранящее список имён друзей
     */
    private ArrayList<String> friendsName = new ArrayList();

    /**
     * Поле, хранящее список битмапов аватарок пользователей
     */
    private ArrayList<Bitmap> friendsAvatarsBitmap = new ArrayList();

    /**
     * Поле, хранящее адаптер друзей для вывода в ListView
     */
    private VKFriendsAdapter adapter = new VKFriendsAdapter();

    /**
     * Метод, выполняющийся при создании activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk);

        friendsList = (ListView) findViewById(R.id.friendsList);
        currentUserTextView = (TextView) findViewById(R.id.currentUserTextView);
        vkCurrentUserAvatar = (ImageView) findViewById(R.id.vkAvatar);

        VK.login(this);
    }

    /**
     * Метод для обработки результатов вызовы vk-activity
     */
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
                System.out.println("Авторизация провалена");
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
                        System.out.println("Запрос провален");
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
                            new AvatarDownloadAsyncTask(VkAvatar.CURRENT, avatarUrl).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void fail(@NotNull Exception e) {
                        System.out.println("Запрос провален");
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

                                friendsName.add(friend.getString("first_name") + " " + friend.getString("last_name"));

                                new AvatarDownloadAsyncTask(VkAvatar.FRIEND, friend.getString("photo_200_orig")).execute();
                            }

                            friendsList.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void fail(@NotNull Exception e) {
                        System.out.println("Запрос провален");
                    }
                });
            }
        };

        VK.onActivityResult(requestCode, resultCode, data, callback);

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * AsyncTask для загрузки аватарок VK
     * @autor Миколенко Евгений (Fertnam)
     * @version 2
     */
    class AvatarDownloadAsyncTask extends Image {
        private VkAvatar mode;

        /**
         * Конструктор, в котором создаётся AsyncTask
         * @param mode - режим загрузки аватарки
         * @param url - url аватарки
         */
        public AvatarDownloadAsyncTask(final VkAvatar mode, final String url) {
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
            switch (mode){
                case CURRENT: vkCurrentUserAvatar.setImageBitmap(imageBitmap); break;
                case FRIEND: friendsAvatarsBitmap.add(imageBitmap);
                             adapter.notifyDataSetChanged();
                             break;
            }
        }
    }

    /**
     * Adapter для вывода списка друзей VK
     * @autor Миколенко Евгений (Fertnam)
     * @version 2
     */
    class VKFriendsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return friendsName.size();
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
                textView.setText(friendsName.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
}
