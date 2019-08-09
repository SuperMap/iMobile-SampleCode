package com.supermap.imobile.Pop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.supermap.data.FieldInfo;
import com.supermap.data.FieldInfos;
import com.supermap.data.Recordset;
import com.supermap.imobile.adapter.AttributeAdapter;
import com.supermap.imobile.fragment.MyDatasCollectorFragment;
import com.supermap.imobile.myapplication.R;
import com.supermap.mapping.MapControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 属性表
 */
public class AttributePop extends PopupWindow {

    public MyDatasCollectorFragment myDatasCollectorFragment;
    String RootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    private View view;
    private Context context;
    private RecyclerView recyclerView;
    private MapControl mapControl;
    Recordset recordset;
    TextView btn_takephoto;//拍照
    TextView btn_takeaudio;//录音
    TextView btn_takevideo;//录像
    TextView btn_showphoto;//查看相片
    TextView btn_showaudio;//查看音频
    TextView btn_showvideo;//查看视频
    TextView btn_editfieldvalue;//编辑字段值
    TextView btn_save;//保存
    ImageView image_photo;//相片显示
    VideoView videoView;//视频显示
    Button btn_closeMedia;//关闭多媒体
    Button btn_goback;//返回
    RelativeLayout layout_view;//多媒体视图
    AttributeAdapter adapter = null;
    int indexPos;//当前点击位置
    String caption;//当前点击位置

    public AttributePop(MapControl mapControl, Context context, Recordset recordset, int height, int width) {
        this.mapControl = mapControl;
        this.context = context;
        this.recordset = recordset;
        initView();
        setWidth(width);
        setHeight(height/2);
        setContentView(view);
        setAnimationStyle(R.style.AnimationRightFade);//设置弹出样式
        setOutsideTouchable(false);
    }

    private void initView() {
        view = LayoutInflater.from(context).inflate(R.layout.pop_attribute, null);//设置显示视图
        recyclerView = view.findViewById(R.id.recycler_attrbute);;

        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        adapter = new AttributeAdapter(recordset);
        //设置图层属性点击事件
        adapter.setOnItemListener(new AttributeAdapter.OnItemListener() {
            @Override
            public void onClick(int pos,String Caption) {
                adapter.setDefSelect(pos);//设置当前位置
                indexPos = pos;//设置当前位置
                caption=Caption;

            }
        });
        recyclerView.setAdapter(adapter);

        //绑定控件
        btn_takephoto = (TextView) view.findViewById(R.id.btn_takephoto);
        btn_takeaudio = (TextView) view.findViewById(R.id.btn_takeaudio);
        btn_takevideo = (TextView) view.findViewById(R.id.btn_takevideo);
        btn_showphoto = (TextView) view.findViewById(R.id.btn_showphoto);
        btn_showaudio = (TextView) view.findViewById(R.id.btn_showaudio);
        btn_showvideo = (TextView) view.findViewById(R.id.btn_showvideo);
        btn_editfieldvalue = (TextView) view.findViewById(R.id.btn_editfieldvalue);
        btn_save = (TextView) view.findViewById(R.id.btn_save);
        btn_closeMedia = (Button) view.findViewById(R.id.btn_close);
        btn_goback = (Button) view.findViewById(R.id.btn_goback);
        image_photo = (ImageView) view.findViewById(R.id.image_photo);
        layout_view = (RelativeLayout) view.findViewById(R.id.layout_view);
        videoView = (VideoView) view.findViewById(R.id.video_view);

        //设置点击事件监听
        btn_takephoto.setOnClickListener(listener);
        btn_takeaudio.setOnClickListener(listener);
        btn_takevideo.setOnClickListener(listener);
        btn_showphoto.setOnClickListener(listener);
        btn_showaudio.setOnClickListener(listener);
        btn_showvideo.setOnClickListener(listener);
        btn_editfieldvalue.setOnClickListener(listener);
        btn_closeMedia.setOnClickListener(listener);
        btn_save.setOnClickListener(listener);
        btn_goback.setOnClickListener(listener);


    }

    /**
     * pop显示
     */
    public void show() {
        int[] location = new int[2];
        mapControl.getRootView().getLocationInWindow(location);
        showAtLocation(mapControl.getRootView(), Gravity.BOTTOM , 0, 0);
    }

    int Flag = 0;//设置标识符
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_takephoto:

                    Flag = 1;
                    Dialogshow();
                    break;
                case R.id.btn_takeaudio:
                    Flag = 2;
                    Dialogshow();
                    break;
                case R.id.btn_takevideo:
                    Flag = 3;
                    Dialogshow();
                    break;
                case R.id.btn_showphoto:
                    openLocalPhoto();

                    break;
                case R.id.btn_showaudio:
                    openLocalAudio();
                    break;
                case R.id.btn_showvideo:
                    openLocalVideo();

                    break;
                case R.id.btn_close:
                    closeMedia();
                    break;
                case R.id.btn_editfieldvalue:
                    Flag = 4;
                    editFieldValue();
                    break;
                case R.id.btn_save:
                    dismiss();
                    break;
                case R.id.btn_goback:
                    dismiss();
                    break;
            }
        }
    };

    private void closeMedia() {
        layout_view.setVisibility(View.GONE);
        image_photo.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    /**
     * 弹出对话框，设置采集信息内容名称
     */
    public void Dialogshow() {
        View view = LayoutInflater.from(context).inflate(R.layout.rename_dialog, null);
        final EditText editText = view.findViewById(R.id.edittext_rename);
        new AlertDialog.Builder(mapControl.getContext())
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editText.getText().toString().equals("")) {
                            shown("名称不能为空");
                            return;
                        }

                        //当为1时，采集图片信息
                        if (Flag == 1) {
                            recordset.edit();//当前记录集开始编辑
                            recordset.setFieldValue("图片", editText.getText().toString() + ".jpg");//设置当前字段内容
                            recordset.update();//记录集更新
                            openCamera(editText.getText().toString());//打开拍照功能
                            adapter.refresh();//刷新数据
                        }
                        //当为2时，采集音频
                        else if (Flag == 2) {
                            recordset.edit();
                            recordset.setFieldValue("音频", editText.getText().toString() + ".amr");
                            recordset.update();
                            openRecord(editText.getText().toString());//打开录音功能
                            adapter.refresh();
                        }
                        //当为3时，采集视频
                        else if (Flag == 3) {
                            recordset.edit();
                            recordset.setFieldValue("视频", editText.getText().toString() + ".mp4");
                            recordset.update();
                            openVedio(editText.getText().toString());//打开录像功能
                            adapter.refresh();
                        }
                        //当为4时，重设非系统字段的字段值
                        else if (Flag == 4) {
                            if (caption.equals("级别")){
                                try {
                                    int a=Integer.valueOf(editText.getText().toString());
                                    recordset.edit();
                                    recordset.setFieldValue(caption, a);
                                    recordset.update();
                                    adapter.refresh();
                                }
                                catch (Exception e){
                                    Toast.makeText(context,"该字段值为32位整型",Toast.LENGTH_SHORT).show();
                                }

                            }
                            else {
                                recordset.edit();
                                recordset.setFieldValue(caption, editText.getText().toString());
                                recordset.update();
                                adapter.refresh();
                            }

                        }

                    }
                })
                .setCancelable(true)//点击屏幕外关闭
                .show();
    }

    /**
     * @param s 提示信息
     */
    private void shown(String s) {
        Toast.makeText(mapControl.getContext(), s, Toast.LENGTH_SHORT).show();
    }

    public static final int TAKE_PHOTO = 1;
    public static final int TAKE_AUDIO = 2;
    public static final int TAKE_VEDIO = 3;
    public Uri imageUri;
    public String photopath = null;

    /**
     * 开启拍照功能
     *
     * @param name 输出照片名称
     */
    private void openCamera(String name) {
        File file = new File(RootPath + "/Mobile GIS/Media/photo");//创建照片指定输出位置
        if (!file.exists()) {
            file.mkdirs();//如果当前文件夹不存在，则新建
        }

        File outputImage = new File(file, name + ".jpg");//创建当前输出照片
        try {
            //如果文件存在则删除，从新创建一个新的文件
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
            photopath = outputImage.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(context,
                    "com.supermap.imobile.myapplication.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //指定图片的输出地址
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //使用startActivityForResult()方法启动活动，因此拍完照片后会有结果返回到onActivityResult()方法中
        //如果发现拍照成功，就可以调用BitmapFactory的decodeStream()方法将ouput_iamge.jpg这张照片解析成Bitmap
        //对象，然后把它设置到ImageView中显示出来
        myDatasCollectorFragment.startActivityForResult(intent, TAKE_PHOTO);
    }

    public String audiopath = null;

    /**
     * 开启录音功能
     *
     * @param name
     */
    private void openRecord(String name) {
        File file = new File(RootPath + "/Mobile GIS/Media/audio");//创建照片指定输出位置
        if (!file.exists()) {
            file.mkdirs();//如果当前文件夹不存在，则新建
        }

        File outputaudio = new File(file, name + ".amr");
        audiopath = outputaudio.toString();
        try {
            //如果文件存在则删除，从新创建一个新的文件
            if (outputaudio.exists()) {
                outputaudio.delete();
            }
            outputaudio.createNewFile();
            outputaudio.setWritable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //启动录音程序
        Intent intent = new Intent();
        intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        myDatasCollectorFragment.startActivityForResult(intent, TAKE_AUDIO);
    }

    public String vediopath = null;

    /**
     * 开启录像功能
     *
     * @param name
     */
    private void openVedio(String name) {
        File file = new File(RootPath + "/Mobile GIS/Media/vedio");//创建照片指定输出位置
        if (!file.exists()) {
            file.mkdirs();//如果当前文件夹不存在，则新建
        }

        File outputvedio = new File(file, name + ".mp4");
        vediopath = outputvedio.toString();
        try {
            //如果文件存在则删除，从新创建一个新的文件
            if (outputvedio.exists()) {
                outputvedio.delete();
            }
            outputvedio.createNewFile();
            outputvedio.setWritable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //启动录像程序
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        myDatasCollectorFragment.startActivityForResult(intent, TAKE_VEDIO);
    }

    /**
     * 查看采集的相片
     */
    private void openLocalPhoto() {

        String path = (String) recordset.getFieldValue("图片");//先获取属性表图片字段对应图片名称
        if (path != null) {//若是不为空，则根据照片位置+名称显示图片
            String photopath = RootPath + "/Mobile GIS/Media/photo/" + path;
            try {
                FileInputStream fileInputStream = new FileInputStream(photopath);//设置照片输入位置
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;//设置缩放倍数
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream, null, options);
                image_photo.setImageBitmap(bitmap);//设置显示图片
                layout_view.setVisibility(View.VISIBLE);
                image_photo.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                if (videoView.isPlaying()) {//如果当前有视频处于播放，则停止播放
                    videoView.pause();
                }
                if (mediaPlayer != null) {//如果当前有音频处于播放，则停止播放
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "请先采集图片", Toast.LENGTH_SHORT).show();
        }
    }

    MediaPlayer mediaPlayer = null;

    /**
     * 打开本地音频
     */
    private void openLocalAudio() {
        String path = (String) recordset.getFieldValue("音频");//先获取属性表图片字段对应图片名称
        if (path != null) {//若是不为空，则根据照片位置+名称显示图片
            String audiopath = RootPath + "/Mobile GIS/Media/audio/" + path;
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audiopath);//设置音频数据
                mediaPlayer.prepare();//音频准备完成
                mediaPlayer.start();//开始播放
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "请先采集音频", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开本地视频
     */
    private void openLocalVideo() {
        String path = (String) recordset.getFieldValue("视频");//先获取属性表图片字段对应图片名称
        if (path != null) {//若是不为空，则根据照片位置+名称显示图片
            String videopath = RootPath + "/Mobile GIS/Media/vedio/" + path;
            layout_view.setVisibility(View.VISIBLE);
            image_photo.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(videopath);//设置视频数据
            videoView.start();//开始播放
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } else {
            Toast.makeText(context, "请先采集视频", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 编辑字段值
     */
    private void editFieldValue() {
        boolean isselect = adapter.getItemSelect();//获取当前是否有item被选中
        if (!isselect) {//如果没有选中
            Toast.makeText(context, "请选择编辑字段", Toast.LENGTH_SHORT).show();
        } else {
            FieldInfos fieldInfos = recordset.getFieldInfos();
            FieldInfo fieldInfo = fieldInfos.get(caption);//获取当前点击item对应位置
            String fieldname = fieldInfo.getName();//获取当前点击item对应的字段
            if (fieldname.startsWith("Sm") || fieldname.equals("图片") || fieldname.equals("音频") || fieldname.equals("视频")) {
                Toast.makeText(context, "系统字段，不可编辑", Toast.LENGTH_SHORT).show();
            } else {
                Dialogshow();
            }
        }
    }

}
