# What is the widget project
It might contain a few custom views or other things I am interested in, which I will add from time to time.
I will use Kotlin to implement all the libraries and corresponding demos in this project, sometimes, 
I would also refer to articles and demos on the internet (e.g., excellent projects on Git Hub), some of the libraries might be small demos that I have used in previous projects and worth sharing with others.
In principle, the custom view part is not dependent on a third-party lib, use Kotlin to implement.

# CustomView Library
Currently, the library contains one custom view, CountdownCircleView. This is a tiny custom view, 
which will be in the Splash Screen of the Android App, and will allow the Splash Screen (contains ADs) to stay for 3 to 5 seconds, 
which is very common in the Android market App in China. I used more than 10 popular Apps in the China Application market.
Most of them have the Splash Screen with a countdown view, some are simple, just the text "Skip" + countdown number;
Some apps are fancy (Use animation on circle view). Take a look at the function of this custom view.

## The gif of demo
<img src="https://github.com/tancolo/widget/blob/main/resource/CountdownCircleView_records.gif" alt="the gif for demo" width="270" height="480">

## The "Skip ADs" view in different apps on the China app market
<img src="https://github.com/tancolo/widget/blob/main/resource/rectangle-001.png" alt="" width="640" height="336">

<img src="https://github.com/tancolo/widget/blob/main/resource/rectangle-002.png" alt="" width="640" height="336">

<img src="https://github.com/tancolo/widget/blob/main/resource/rectangle-003.png" alt="" width="640" height="336">

<img src="https://github.com/tancolo/widget/blob/main/resource/circle-arc.png" alt="" width="446" height="448">

## How to use it?
Currently, the library is not published to the repository, you need to copy the files into your App separately 
or import the library into your project. The core files are CountdownCircleView.kt + attrs.xml.
Just add the view into your layout XML and add some attributes. See the attrs.xml or file CountdownCircleView.kt
You can customize the size, background, text size/color, and draw-type for the arc.
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
When the animation finishes, it calls the callback function complete(), and does something. you want.


# Acknowledgment
1. https://github.com/zhazhaxin/CountdownView
2. https://github.com/SuperKotlin/CountDownView


