# WORK WITH OBSERVABLE DATA OBJECTS

[OBSERVABLE DATA OBJECT GOOGLE DOCUMENT](https://developer.android.com/topic/libraries/data-binding/observability)

## ONE-WAY DATA BINDING
- trong DataBinding, để ràng buộc dữ liệu ta sử dụng Observable Data Object
- __ràng buộc dữ liệu 1 chiều là khi data ở backend thay đổi, nó sẽ dẫn đến việc update lại nội dung trên UI__
- Observability cung cấp khả năng cho 1 object có thể thông báo cho object khác về sự thay đổi của nó, gọi là ràng buộc dữ liệu, và việc thông báo này hoàn toàn tự động, không như những cách liên kết dữ liệu cũ sẽ không tự động cập nhật lại UI
- nghĩa là khi sử dụng Observable, thì các object, field, collection, sẽ luôn được cập nhật tức thời dữ liệu, khi có 1 sự kiện nào đó xảy ra mà yêu cầu nó cập nhật lại dữ liệu
- Data Binding Library cho phép cung cấp khả năng Observable cho các: object, field, collection
- ràng buộc dữ liệu có 2 loại
    - 1 chiều
    - 2 chiều
- để sử dụng cơ chế của __Observable__ thì ta phải cài đặt __dataBinding__ trong __Gradle module:app__
```js
plugins {
    id 'com.android.application'
}

android {

// ...
    buildFeatures {
        dataBinding true
    }
}

//...
```

### CẤU TRÚC CHƯƠNG TRÌNH

- Architecture Pattern thường được sử dụng với DataBinding là MVVM
- Gradle khai báo sử dụng dataBinding
- ViewModel extends BaseObservable
- layout (VD: activity_main) cài đặt dataBinding đến ViewModel
- class điều khiển layout làm trung gian giữa layout và ViewModel (VD: MainActivity)

### OBSERVABLE INTERFACE & VIEW MODEL CLASS

- đầu tiên ta dựng class ViewModel extends BaseObservable
- __MainViewModel.java__
```java
public class MainViewModel extends BaseObservable {
    // ...
}
```
- giả sử ta class MainViewModel chỉ có 1 field đơn giản để ta thực hành là ``String message`` đồng thời ta cũng khai báo __Constructor__, __Getter__, __Setter__ cho nó
```java
public class MainViewModel extends BaseObservable {
    private String message;

    public MainViewModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

- định nghĩa 1 Method để ta xử lý logic, ví dụ khi user click vào Button thì sẽ kích hoạt method và thay đổi nội dung của TextView

```java
public class MainViewModel extends BaseObservable {
    private String message;

    public MainViewModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void changeValueTextView() {
        this.setMessage("New TextValue");
    }
}
```

- nhưng, chỉ như vậy thì việc ràng buộc dữ liệu vẫn chưa xảy ra, nghĩa là khi method ``changeValueTextView()`` được gọi thì TextView vẫn không thể update lại dữ liệu
- để có thể ràng buộc dữ liệu với việc sử dụng interface ``BaseObservable`` thì
    - __Getter__ ta thêm ``anotation`` trước tên method: __@Bindable__
    - __Setter__ sau khi gán data ta thêm: __notifyPropertyChanged(BR.TÊN_FIELD);__

```java
public class MainViewModel extends BaseObservable {
    private String message;

    public MainViewModel(String message) {
        this.message = message;
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        notifyPropertyChanged(BR.message);
    }

    public void changeValueTextView() {
        this.setMessage("New TextValue");
    }
}
```

### XỬ LÝ LAYOUT

- ta sẽ dùng layout của __MainActivity__ cho cơ chế __DataBinding__ và bind đến __MainViewModel__
- trong layout này ta sẽ dựng 1 TextView, 1 Button
- ở thuộc tính __android:text__ của TextView ta sẽ khai báo là field __message__ của MainViewModel: 

```
android:text="@{MainViewModel.message}"
```

- khai báo thuộc tính __android:onClick__ cho Button là biểu thức sử dụng cơ chế __Listener Binding__ để gọi đến method của MainViewModel: 

```
android:onClick="@{() -> MainViewModel.changeValueTextView()}"
```

- __activity_main.xml__
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>

        <variable
            name="MainViewModel"
            type="com.example.workwithobservabledataobjects.MainViewModel" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:padding="10dp">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@{MainViewModel.message}"
            android:textSize="40sp"
            android:textStyle="bold" />

        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:onClick="@{() -> MainViewModel.changeValueTextView()}"
            android:text="@string/change_value" />
    </LinearLayout>
</layout>
```

### MAIN ACTIVITY - BINDING LAYOUT & VIEW MODEL

- ở class MainActivity quản lý layout activity_main, ta thực hiện bind layout và ViewModel với nhau
- __MainActivity.java__
```java
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        MainViewModel mainViewModel = new MainViewModel("Observable Tutorial");
        mActivityMainBinding.setMainViewModel(mainViewModel);

        setContentView(mActivityMainBinding.getRoot());
    }
}
```

- sau khi build chương trình, ta sẽ thấy TextView hiển thị __Observable Tutorial__, nhưng khi click vào Button thì TextView sẽ update lại là __New TextValue__, mặc dù ta không hề định nghĩa 1 dòng code nào để cập nhật lại TextView, mọi việc chỉ là khai báo theo cấu trúc của __DataBinding__ và __Observable__

## TWO-WAY DATA BINDING

- __ràng buộc dữ liệu 2 chiều là khi, data ở backend thay đổi thì nội dung trên UI cũng thay đổi, và nếu user tác động nội dung trên UI thì data ở backend cũng thay đổi__
- ví dụ:
    - khi user click Button, chương trình sẽ thay đổi data và cập nhật nội dung trên EditText
    - và khi user chỉnh sửa nội dung trên EditText, thì data ở chương trình cũng sẽ update lại
- để có thể kiểm chứng ta thực hiện ví dụ sau:
    - đầu tiên chỉnh sửa lại layout gồm 1 EditText và 1 Button
    - khai báo __id__ cho EditText để có thể gọi thông qua object DataBinding trong chương trình (việc này chỉ nhằm kiểm chứng ràng buộc dữ liệu 2 chiều hoạt động như thế nào, chứ nó không phải là phương pháp sử dụng ràng buộc dữ liệu 2 chiều)
    - ở MainActivity, gọi method __addTextChangedListener__ của EditText và truyền vào interface __new TextWatcher()__, việc này nhằm lấy được sự thay đổi của EditText để kiểm tra xem ràng buộc dữ liệu 2 chiều có hoạt động hay không
    - ở method được override __afterTextChanged()__ ta thực hiện gọi __Log__
        - 1 Log dùng để hiển thị nội dung mà __TextWatcher__ trả về, tức là sự thay đổi trên UI mỗi khi EditText thay đổi
        - 1 Log dùng để hiển thị nội dung data của __MainViewModel.message__ để xem data có update theo nội dung của UI mỗi khi thay đổi hay không
    - cuối cùng, ở layout, thuộc tính __android:text__ của EditText ta đang ràng buộc EditText này đến field message của MainViewModel, nhưng chỉ 1 chiều, để có thể khai báo rằng đây là ràng buộc dữ liệu 2 chiều ta thêm dấu __=__ vào giữa ``@`` và ``{``

```
android:text="@={MainViewModel.message}"
```

- __activity_main.xml__
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>

        <variable
            name="MainViewModel"
            type="com.example.workwithobservabledataobjects.MainViewModel" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:padding="10dp">

        <EditText
            android:id="@+id/edt_message"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@={MainViewModel.message}"
            android:textSize="40sp"
            android:textStyle="bold" />

        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:onClick="@{() -> MainViewModel.changeValueTextView()}"
            android:text="@string/change_value" />
    </LinearLayout>
</layout>
```

- __MainActivity.java__
```java
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        MainViewModel mainViewModel = new MainViewModel("Observable Tutorial");
        mActivityMainBinding.setMainViewModel(mainViewModel);

        mActivityMainBinding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("EdiText", s.toString());
                Log.e("MainViewModel", mainViewModel.getMessage());
            }
        });

        setContentView(mActivityMainBinding.getRoot());
    }
}
```

- khi build chương trình, bật Logcat Error, mỗi khi ta thay đổi nội dung trên EditText thì nội dung cả 2 Tag EdiText và MainViewModel cũng thay đổi nội dung Log theo, khi ta click Button cũng tương tự, như vậy chính là ràng buộc dữ liệu 2 chiều, cả 2 phía, user và backend đều có thể nội dung của nhau

> ràng buộc dữ liệu 2 chiều sẽ có thêm dấu __=__ sau __@__

## OBSERVABLE FIELD

- để ràng buộc dữ liệu 1 chiều hoặc 2 chiều ta khai báo anotation trước Getter
```
@Bindable
```
- và thêm câu lệnh dưới đây sau khi gán data trong Setter
```
notifyPropertyChanged(BR.message);
```
___

- ngoài ra, để ràng buộc dữ liệu, ta còn 1 cách khác để sử dụng mà không cần đánh dấu anotation ``@Bindable`` và thêm lệnh ``notifyPropertyChanged()`` chính là sử dụng Observable Field
- có rất nhiều loại và cách sử dụng Observable Field
    - ObservableBoolean
    - ObservableByte
    - ObservableChar
    - ObservableShort
    - ObservableInt
    - ObservableLong
    - ObservableFloat
    - ObservableDouble
    - ObservableParcelable
- để sử dụng Observable Field cho kiểu object thì sử dụng cú pháp ví dụ dữ liệu kiểu String như sau
```
public final ObservableField<String> firstName = new ObservableField<>();
```
- ví dụ như sau đối với Observable Field
```java
private static class User {
    public final ObservableField<String> firstName = new ObservableField<>();
    public final ObservableField<String> lastName = new ObservableField<>();
    public final ObservableInt age = new ObservableInt();
}
```
- để truy cập vào giá trị các field trên ta có thể sử dụng method ``set()`` hoặc ``get()``
```
user.firstName.set("Google");
int age = user.age.get();
```

___

- đối với các Observable Collection ta có 
    - ``ObservableArrayMap<Key, Value>``
    - ``ObservableArrayList<Object>``
- ví dụ đối với Observable Collection Map
```java
ObservableArrayMap<String, Object> user = new ObservableArrayMap<>();
user.put("firstName", "Google");
user.put("lastName", "Inc.");
user.put("age", 17);
```
- trong layout, map có thể được tìm thấy bằng chuỗi Key, ví dụ
```xml
<data>
    <import type="android.databinding.ObservableMap"/>
    <variable name="user" type="ObservableMap&lt;String, Object&gt;"/>
</data>
…
<TextView
    android:text="@{user.lastName}"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
<TextView
    android:text="@{String.valueOf(1 + (Integer)user.age)}"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```
- ví dụ đối với Observable Collection List
```java
ObservableArrayList<Object> user = new ObservableArrayList<>();
user.add("Google");
user.add("Inc.");
user.add(17);
```
- trong layout, list có thể được truy cập thông qua Index, ví dụ
```xml
<data>
    <import type="android.databinding.ObservableList"/>
    <import type="com.example.my.app.Fields"/>
    <variable name="user" type="ObservableList&lt;Object&gt;"/>
</data>
…
<TextView
    android:text='@{user[Fields.LAST_NAME]}'
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
<TextView
    android:text='@{String.valueOf(1 + (Integer)user[Fields.AGE])}'
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```

> KHI SỬ DỤNG OBSERVABLE FIELD, ACCESS MODIFIER CỦA CÁC FIELD LUÔN LUÔN LÀ <br/>
> ``public final``

### VÍ DỤ VỀ SỬ DỤNG OBSERVABLE FIELD

#### MAIN VIEW MODEL
___
- ở ví dụ trên, trong MainViewModel ta có field ``message`` là sử dụng cách ràng buộc dữ liệu thông thường
- ta sẽ khai báo 1 field mới và sử dụng Observable Field đối với field mới khai báo để ràng buộc dữ liệu 2 chiều, lưu ý là access modifier của nó luôn là ``public final``
- đồng thời ta định nghĩa 1 method để khi click vào Button sẽ gọi đến method này và thay đổi nội dung trên EditText, mà lúc này để thay đổi giá trị của Observable Field chỉ có cách là gọi method ``set()``
- __MainViewModel__
```java
public class MainViewModel extends BaseObservable {
    //...
    public final ObservableField<String> contentObservableField = new ObservableField<>();

    //...
    public void changeContentObservableField() {
        this.contentObservableField.set("Change Content Observable Field");
    }
}
```
> nhận xét: đối với Observable Field ta đã tiết kiệm được việc khai báo Setter, Getter

#### LAYOUT ACTIVITY MAIN
___
- trên layout activity_main, ta thực hiện ràng buộc dữ liệu 2 chiều bình thường đối với field Observable, chỉ có việc thay đổi field cần trỏ đến ở các ví dụ trước bằng field observable
- __activity_main__
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>

        <variable
            name="MainViewModel"
            type="com.example.workwithobservabledataobjects.MainViewModel" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:padding="10dp">

        <EditText
            android:id="@+id/edt_message"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@={MainViewModel.contentObservableField}"
            android:textSize="40sp"
            android:textStyle="bold" />

        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:onClick="@{() -> MainViewModel.changeContentObservableField()}"
            android:text="@string/change_value" />
    </LinearLayout>
</layout>
```
- ban đầu EditText sẽ hiển thị nội dung field observable của MainViewModel là ``contentObservableField``
- khi click vào Button, method ``changeContentObservableField()`` sẽ được gọi và thực thi logic thay đổi nội dung của field ``contentObservableField`` như đã định nghĩa trong MainViewModel

#### MAIN ACTIVITY
___

- tất nhiên là khi build chương trình lên, EditText sẽ không chứa dữ liệu nào, vì chưa có bất kỳ dòng code nào gán giá trị ban đầu cho field observable của MainViewModel, chỉ có code gán giá trị khi click vào Button
- để EditText hiển thị giá trị ban đầu của field observable, trong MainActivity, ngay sau khi MainViewModel được khởi tạo, ta gọi method ``set()`` để gán giá trị cho field observable, sau đó khi build chương trình, EditText sẽ có giá trị ban đầu để hiển thị, như biểu thức ràng buộc dữ liệu 2 chiều đã khai báo trong layout
- đồng thời để kiểm tra ràng buộc dữ liệu 2 chiều có hoạt động hay không, ở listener ``addTextChangedListener`` ta chỉnh sửa lại lệnh ``Log.e`` của Tag ``MainViewModel`` sẽ gọi method ``get()`` để lấy ra giá trị của field observable, mỗi khi nội dung trên EditText thay đổi, thì field observable có thay đổi theo hay không
```java
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        MainViewModel mainViewModel = new MainViewModel("Observable Tutorial");
        mainViewModel.contentObservableField.set("Content Observable Field");
        mActivityMainBinding.setMainViewModel(mainViewModel);

        mActivityMainBinding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("EdiText", s.toString());
                Log.e("MainViewModel", mainViewModel.contentObservableField.get());
            }
        });

        setContentView(mActivityMainBinding.getRoot());
    }
}
```
