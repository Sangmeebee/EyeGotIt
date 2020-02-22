# Map 액티비티 소개

##  MapActivity.java _ Shake 감지
  
### 1. Shake 감지가 이루어질 액티비티(MapActivity)에 SensorEventListener 라는 인터페이스를 implements 시킨다. 
    - 사용자의 움직임을 감지할 수 있도록 하기위함이다
    - 구현해야할 메소드 : onSensorChanged, onAcccuracyChanged 2가지
~~~
public class MapActivity extends AppCompatActivity implements ,,, SensorEventListener {  }
~~~

### 2. SensorManger를 얻어온다. 
    - Context.getSystemService()를 통해서 SensorManger 객체를 얻어올 수 있다
~~~
@Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
~~~
    - 핸드폰의 배터리를 소모시키기 때문에 얻어오는 것(등록) 뿐만 아니라 사용하지 않을때 비활성화 시키는 것은 매우 중요하다

-등록
~~~
@Override
    protected void onStart() {
        ...
        if (accelerormeterSensor != null) {
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }
~~~

-비활성화
~~~
@Override
    protected void onStop() {
        ...
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
~~~

### 3. onSensorChanged 메소드에서 Sensor값을 얻어온다. 
    - 사용자가 핸드폰을 흔들때 어떤 방향으로 흔드는 지에 대한 x,y,z값이다
    - onSensorChanged 메소드의 인자로 들어온 event의 values 라는 배열값들 중 0,1,2번째 값이 x,y,z 축이다
