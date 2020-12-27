package com.mingrisoft.mybird

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class BirdGameView(context: Context?, attr: AttributeSet?) : SurfaceView(context, attr), SurfaceHolder.Callback {
    //控制对象 SurfaceHolder信息
    private var holder: SurfaceHolder? = null
    override fun getHolder(): SurfaceHolder {
        return super.getHolder()
    }

    //画笔
    protected var mPaint: Paint? = null

    //画线点坐标
    var x = 0

    //小鸟高度
    var birdy = 0

    //标识是否第一次
    var k = true

    //自身的大小
    protected var mWidth = 0
    protected var mHeight = 0

    //背景
    private var bj: Bitmap? = null

    //小鸟
    private var bird_im: Bitmap? = null

    //障碍
    private var top_za: Bitmap? = null
    private var bom_za: Bitmap? = null

    //暂停图片
    private var game_over: Bitmap? = null
    private var start_btn: Bitmap? = null

    //图片宽高：
    private var bird_h = 0
    private var bird_w = 0
    private var up_h = 0
    private var up_w = 0
    val randoms = (50..100).random()
    //障碍1 x坐标
    private var za_x1 = 0

    //障碍2 x坐标
    private var za_x2 = 0

    //分数
    private var score = 0

    //绘制线程
    private var threadDrawUI: ThreadDrawUI? = null

    //绘制线程是否开启标识
    private var isDrawUI = true

    //逻辑线程
    private var threadLogic: ThreadLogic? = null

    //逻辑线程是否开启标识
    private var isLogic = true

    //获取自身大小
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //自身的宽
        mWidth = w
        //自身的高
        mHeight = h
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {
        // TODO Auto-generated method stub
    }

    //创建的时候调用方法
    override fun surfaceCreated(holder: SurfaceHolder) {
        // TODO Auto-generated method stub
        //创建绘制文字画笔
        mPaint = Paint()
        //加载图片资源
        game_over = BitmapFactory.decodeResource(resources, R.drawable.bird_gameover)
        start_btn = BitmapFactory.decodeResource(resources, R.drawable.bird_start_btn)
        bj = BitmapFactory.decodeResource(resources, R.drawable.bird_bg)
        bird_im = BitmapFactory.decodeResource(resources, R.drawable.bird_hero)
        //获取小鸟图片宽高用于绘制以及判断
        var bird_im2=bird_im;
        if (bird_im2 != null) {
            bird_h = bird_im2.getHeight()
        }
        if (bird_im2 != null) {
            bird_w = bird_im2.getWidth()
        }
        top_za = BitmapFactory.decodeResource(resources, R.drawable.obstacle_up)
        bom_za = BitmapFactory.decodeResource(resources, R.drawable.obstacle_down)
        //获取障碍宽高用于判断游戏结束用
        var top_za2=top_za;
        if (top_za2 != null) {
            up_h = top_za2.getHeight()
        }
        if (top_za2 != null) {
            up_w = top_za2.getWidth()
        }
        //开启线程标识
        isLogic = true
        isDrawUI = true
        //绘制线程
        threadDrawUI = ThreadDrawUI()
        //逻辑线程
        threadLogic = ThreadLogic()
        //开启线程
        threadDrawUI!!.start()
        threadLogic!!.start()
    }

    //退出结束线程
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isDrawUI = false
        isLogic = false
    }

    //手势判断
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // TODO Auto-generated method stub
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (k) { //判断是否结束游戏
                val startx = event.x
                val starty = event.y
                //判断是否点击重新开始游戏
                if (startx < mWidth / 2 + start_btn!!.width / 2 && startx > mWidth / 2 - start_btn!!.width / 2) {
                    if (starty > mHeight / 2 + game_over!!.height && starty < mHeight / 2 + game_over!!.height + start_btn!!.height) {
                        isDrawUI = true
                        isLogic = true
                        //绘制线程
                        threadDrawUI = ThreadDrawUI()
                        //逻辑线程
                        threadLogic = ThreadLogic()
                        //开启线程
                        threadDrawUI!!.start()
                        threadLogic!!.start()
                    }
                }
            } else { //游戏没结束执行下面方法
                birdy -= 80 //使小鸟向上
            }
        }
        return true
    }

    //绘图线程
    internal inner class ThreadDrawUI : Thread() {
        override fun run() {
            var canvas: Canvas?

            while (isDrawUI) {
                canvas = null
                try {
                    // 锁定整个画布，在内存要求比较高的情况下，建议参数不要为null
                    canvas = holder!!.lockCanvas(null)
                    synchronized(holder!!) { //同步
                        if (isDrawUI) {
                            //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高
                            val rectF = RectF(0.0F, 0.0F, mWidth.toFloat(), mHeight.toFloat())
                            //绘制背景
                            canvas.drawBitmap(bj!!, null, rectF, null)
                            //绘制小鸟
                            canvas.drawBitmap(bird_im!!, mWidth / 2.toFloat(), birdy.toFloat(), null)
                            //随机高度


                            //绘制障碍1
                            canvas.drawBitmap(top_za!!, za_x1.toFloat(), -up_h / 2.toFloat()-80.0F, null)
                            canvas.drawBitmap(bom_za!!, za_x1.toFloat(), mHeight - up_h / 2.toFloat(), null)
                            if (za_x1 < mWidth) {
                                //绘制障碍2
                                canvas.drawBitmap(top_za!!, za_x2.toFloat(), -up_h / 2.toFloat(), null)
                                canvas.drawBitmap(bom_za!!, za_x2.toFloat(), mHeight - up_h / 2.toFloat()-70.0F, null)
                            }
                            mPaint!!.color = Color.RED
                            mPaint!!.textSize = 30f
                            //绘制文字
                            canvas.drawText("分数$score", 100f, 100f, mPaint!!)
                        }
                    }
                } finally { //使用finally语句保证下面的代码一定会被执行
                    if (canvas != null) {
                        //解锁更新屏幕显示内容
                        holder!!.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }
    }

    //逻辑线程
    internal inner class ThreadLogic : Thread() {
        override fun run() {
            while (isLogic) {
                if (k) { //游戏结束后重新开始游戏
                    //设置初始高度
                    birdy = mHeight / 2
                    za_x1 = mWidth
                    za_x2 = -up_w
                    k = false
                } else { //游戏进行中
                    //移动小鸟
                    birdy += 1
                    //移动障碍
                    za_x1--
                    za_x2--
                    //循环障碍1方法
                    if (za_x1 < -up_w) {
                        za_x1 = mWidth
                    }
                    //循环障碍2方法
                    if (za_x1 == mWidth / 2) {
                        za_x2 = mWidth
                    }
                    //设置得分方法
                    if (za_x1 == mWidth / 2 - up_w || za_x2 == mWidth / 2 - up_w) {
                        score += 1
                    }
                    //判断游戏结束方法
                    if (za_x1 < mWidth / 2 + bird_w && za_x1 > mWidth / 2 - up_w) {
                        if (birdy + bird_h < mHeight - up_h / 2 && birdy > up_h / 2-80.0F) {
                        } else {
                            GameOver()
                        }
                    }
                    //判断游戏结束方法
                    if (za_x2 < mWidth / 2 + bird_w && za_x2 > mWidth / 2 - up_w) {
                        if (birdy + bird_h < mHeight - up_h / 2 -70.0F && birdy > up_h / 2) {
                        } else {
                            GameOver()
                        }
                    }
                }
                try {
                    //线程休眠 数值越小 游戏越流畅 相当于游戏帧
                    sleep(5)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    //游戏结束方法
    fun GameOver() {
        //游戏结束绘制图画
        // 锁定整个画布，在内存要求比较高的情况下，建议参数不要为null
        isDrawUI = false
        isLogic = false
        val canvas = holder!!.lockCanvas(null)
        //游戏结束画面
        onGameStop(canvas)
        holder!!.unlockCanvasAndPost(canvas)
        //游戏结束标识
        k = true
        //清空得分
        score = 0
    }

    //绘制游戏结束画面
    private fun onGameStop(canvas: Canvas) {
        val rectF = RectF(0.0F, 0.0F, mWidth.toFloat(), mHeight.toFloat())
        //绘制背景
        canvas.drawBitmap(bj!!, null, rectF, null)
        //绘制游戏结束画面
        canvas.drawBitmap(game_over!!, mWidth / 2 - game_over!!.width / 2.toFloat(),
                mHeight / 2 - game_over!!.height.toFloat(), null)
        //绘制重新开始按钮
        canvas.drawBitmap(start_btn!!, mWidth / 2 - start_btn!!.width / 2.toFloat(),
                mHeight / 2 + game_over!!.height.toFloat(), null)
        mPaint!!.color = Color.WHITE
        mPaint!!.textSize = 50f
        //绘制得分
        canvas.drawText("得分：$score", mWidth / 2 - 100.toFloat(), mHeight / 2 - game_over!!.height * 2.toFloat(), mPaint!!)
    }

    //构造函数
    init {
        // TODO Auto-generated constructor stub
        //创建holder
        holder = getHolder()
        //获得holder回调信息
        var holder2=holder;
        if (holder2 != null) {
            holder2.addCallback(this)
        }
    }
}