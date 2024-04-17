# What is the widget project
It might contain a few custom views or other things I am interesting in, I will add from time to time.
I will use kotlin to implement all the libraries and corresponding demos in this project, sometimes, 
I would also refer to articles and demo on the internet (e.g., excellent projects on github), some of the libraries might be small demos that I have used in previous projects and worth sharing with others.
In principle, the custom view part is not dependent on a third-party lib, use Kotlin to implement.

# CustomView Library
Currently the library contains one custom view, CountdownCircleView. This is a tiny custom view, 
which will be in the Splash Screen of the Android App, and will allow the Splash Screen (contains ADs) to stay for 3 to 5 seconds, 
which is very common in the Android market App in China. I used more then 10 popular Apps in China Application market.
Most of them have the Splash Screen with countdown view, some are simple, just a text "Skip" + countdown number;
some Apps are fancy (Use an animation on circle view). Take a look the function about this custom view.
## [see the gif]

## How to use it?
Currently the library is not published to the repository, you need to copy the files into your App separately, 
or import the library into your project. The core files are CountdownCircleView.kt + attrs.xml.
Just add the view into your layout xml and add some attributes. See the attrs.xml or file CountdownCircleView.kt
You can customize the size, background, text size/color, and the draw type for the arc.
``` xml
<com.tancolo.customview.countdownview.CountdownCircleView
                android:id="@+id/view_clockwise_forward"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"

                <!-- the attributes  -->
                app:countdown_circle_radius="30dp"
                app:countdown_arc_width="2dp"
                app:countdown_arc_color="@color/design_default_color_primary"
                app:countdown_background_color="@color/design_default_color_secondary"
                app:countdown_text_color="@color/red"
                app:countdown_text_size="15sp"
                app:countdown_showing_time="5" // 5 seconds
                app:countdown_showing_time_unit="秒"
                app:countdown_arch_draw_types="clockwise_forward"
                app:countdown_start_draw_positions="top" />
```
```kotlin
// to start the animation
findViewById<CountdownCircleView>(R.id.xxxx).start()
```

**About the Callback**
Assume in xxActivity/xxFragment you used, set the Callback on an object of CountdownCircleView.
```kotlin
        // set Callback
        val circleView =  findViewById<CountdownCircleView>(R.id.view_clockwise_forward_5_004)
        circleView.setCallback(object : Callback {
            override fun complete() {
                Toast.makeText(this@MainActivity, "The Animation finished", Toast.LENGTH_SHORT).show()
            }
        })
```
When the animation finish, it would call the callback function complete(), do sht. you want.


# Acknowledgment
1. https://github.com/zhazhaxin/CountdownView
2. https://github.com/SuperKotlin/CountDownView


