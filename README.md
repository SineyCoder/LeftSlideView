# LeftSlideView
all of the items of the LeftSlideView can slide left to show hidden item.

## first

add repository
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

add dependency
```
dependencies {
  implementation 'com.github.SineyCoder:LeftSlideView:v1.2'
}
```

## use
```
LeftSlideLinearManager manager = new LeftSlideLinearManager(this);//extends LinearLayoutManager
mLeftSlideView.setLayout(R.id.content_item);//basic layout
mLeftSlideView.setItems(R.id.a, R.id.b, R.id.c);//hidden view which you want to show by sliding left
mLeftSlideView.setLayoutManager(manager);//set layout manager
mLeftSlideView.setAdapter(adapter);//set adapter
```

<img width="500" src="https://img-blog.csdnimg.cn/20181209142833361.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2E1NjgyODM5OTI=,size_16,color_FFFFFF,t_70"/>

Detail：according to my blob `https://blog.csdn.net/a568283992/article/details/84927713`
