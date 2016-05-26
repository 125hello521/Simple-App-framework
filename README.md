# Simple-App-framework
个人开发中的一些简单封装，主要是开源的库，原则是不重复造轮子，不依赖轮子
-------------------------------------------------------------------------------------
开发中用到的一些项目，先列出来，然后尝试整合，抽取
eventbus         
EventBus is an open-source library for Android using the publisher/subscriber pattern to solve the problem of loose coupling             https://github.com/greenrobot/EventBus

FlycoRoundView
A library helps Android built-in views easy and convenient to set round rectangle background and accordingly related shape resources can be reduced. 一个扩展原生控件支持圆角矩形框背景的库,可以减少相关shape资源文件使用.
https://github.com/H07000223/FlycoRoundView

square 三件套    okhttp   retrofit   picasso（Glide）
以上3个都是square出品，至于Glide是google推荐，应该算是网络3套装了吧（retrofit基本全是采用接口形式，不喜欢，一开始就是用还好，半路切换就尴尬了，所以这个看自己的了）
-------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------
接下来时自己的一些对于Okhttp的简单封装，OkHttpParams来自async-http，存放body数据。OkhttpUtils是简单的封装，由于个人项目仅用到简单键值对和不同字段、多张上传图片，故仅判断这2种情况。需要的自己进一步修改，个人感觉没必要完全用别人的，满足功能就行。使用方法：
-------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------

          get,post
          OkHttpUtils.post(url, params, new OkHttpHandler() {
             @Override
             void success(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(result);
                    }
                });
               }
              @Override
              void failure() {
                Log.d("vvv", "failure");
              }
           });
            下载文件
             OkHttpUtils.downLoad(jd, null, new OkHttpHandler() {
              @Override
              void progress(final long downloadSize, final long totalSize, final boolean is) {
                super.progress(downloadSize, totalSize, is);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(downloadSize + "---" + totalSize + "---" + is);
                    }
                });
              }
            
              @Override
              void success(String result) {}
           
              @Override
              void success(InputStream inputStream) {
                 //File file = new File("/mnt/sdcard/abc.apk");
                 //if (!file.exists()) {
                    //try {
                        //file.createNewFile();
                    //} catch (IOException e) {
                        //e.printStackTrace();
                    //}
                 //}
                 //OkHttpFileUtils.inputstream2file(inputStream, file);
              }

              @Override
              void failure() {}
        });
-------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------
Mark:enqueue(CallBack call)是非ui线程，返回结果要在ui线程展示需要用runOnUiThread()方法，目前写到这里
