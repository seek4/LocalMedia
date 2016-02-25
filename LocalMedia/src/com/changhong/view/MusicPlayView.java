package com.changhong.view;

import com.changhong.localmedia.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 代码说明�? 1. 注释掉了磁盘�?起转动的效果，现在的方案不是�?好的，建议若是想实现，可以把圆形封面和磁盘合并成�?张图(�?好在CicicleImageView这里面做)。给�?个旋转动画�?�两个动画，两个View，帧的频率不会那么高
 *        2. 注释掉了上一首�?�下�?首切换的时�?�渐变的动画效果，原图从1-0 ，新图从0-1 的渐变�?? 在模拟器上会报错，主要是因为改变ImageView的背景那�?行报错�??
 * @author sym
 *
 */

public class MusicPlayView extends RelativeLayout {
	
	private Context mContext;
	//旋转一周所用时间
	private static final int ROTATE_TIME = 12 * 1000;
	//动画旋转重复执行的次数，这里代表无数次，似乎没有无限执行的属性，所以用了一个大数字代表
	private static final int ROTATE_COUNT = 10000;
	
	//唱针动画时间
	private static final int NEEDLE_TIME = 1 * 500 ;
	//唱针动画执行的角度
	private static final int NEEDLE_RADIUS = 30 ;
	
	//封面、背景切换时候的渐变动画
	private static final int AVATART_DISC_ALPHA_TIME = 1 * 300 ;
	
	private static final float AVATART_DISC_ALPHA_PERCENT = 0.3f;
	
	//背景
	private ImageView mBackground ;
	
	//唱针
	private ImageView mNeedle;
	//唱片
//	private ImageView mDisc;
	//封面
	private CircleImageView mAvatar;
	
	private boolean isPlay = false;
	
	//唱针移动动画
	ObjectAnimator mAniNeedle;
	
	//磁盘和封面旋转动画 
//	ObjectAnimator mAniDisc;
	ObjectAnimator mAniAvatar;
	
//	//封面更换时的渐变效果
//	ObjectAnimator mAniAlphaAvatarHide;
//	ObjectAnimator mAniAlphaAvatarShow;
//	
//	//背景更换时的渐变效果
//	ObjectAnimator mAniAlphaDiscBgHide;
//	ObjectAnimator mAniAlphaDiscBgShow;
	
	float mValueAvatar ;
	float mValueDisc ;
	float mValueNeedle ;
	
//	private int mCurrentImageResource = 0;
	
//	public MusicPlay mMusicPlayListener ;
	
	public MusicPlayView(Context context, AttributeSet attrs) {
		super(context);
		mContext = context;
	}

	public MusicPlayView(Context context) {
		super(context);
		mContext = context;
	}
	
	public MusicPlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	
	/*public interface MusicPlay{
		void onAvatarChange();
		
		void onDiscbgChange();
	}*/
	
	/*public void setMusicPlayerListener(MusicPlay listener ){
		this.mMusicPlayListener = listener ;
	}*/
	
	
	 @Override
     protected void onFinishInflate() {
        super.onFinishInflate();
        mBackground = (ImageView) findViewById(R.id.bg);
		
		mAvatar = (CircleImageView) findViewById(R.id.avatar);
		
//		Bitmap conformBitmap = toConformBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fm_play_disc), mAvatar.getBitmap());
//		mAvatar.setImageBitmap(conformBitmap);
//		mAvatar.setBackgroundDrawable(new BitmapDrawable(conformBitmap));
		
//		mAvatar.setBackgroundDrawable(getResources().getDrawable(R.drawable.fm_play_disc));
//		mDisc = (ImageView) findViewById(R.id.disc);
		mNeedle = (ImageView) findViewById(R.id.needle);
		
//		mDisc.setVisibility(View.GONE);
		
		initAvatarAnimation(0f);
//		initDiscAnimation(0f);
		initNeedleAnimation(0f);
		
//		//封面页面动画
//		mAniAlphaAvatarHide = ObjectAnimator.ofFloat(mAvatar, "alpha", 1, AVATART_DISC_ALPHA_PERCENT).setDuration(AVATART_DISC_ALPHA_TIME);
//		mAniAlphaAvatarHide.addListener(avatarAlphaHideListener);
//		mAniAlphaAvatarShow = ObjectAnimator.ofFloat(mAvatar, "alpha", AVATART_DISC_ALPHA_PERCENT, 1).setDuration(AVATART_DISC_ALPHA_TIME);
//		
//		mAniAlphaDiscBgHide = ObjectAnimator.ofFloat(mBackground, "alpha", 1, AVATART_DISC_ALPHA_PERCENT).setDuration(AVATART_DISC_ALPHA_TIME);
//		mAniAlphaDiscBgHide.addListener(discbgAlphaHideListener);
//		mAniAlphaDiscBgShow = ObjectAnimator.ofFloat(mBackground, "alpha", AVATART_DISC_ALPHA_PERCENT, 1).setDuration(AVATART_DISC_ALPHA_TIME);
    }
	
	/* AnimatorListener avatarAlphaHideListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator arg0) {
			
		}
		
		@Override
		public void onAnimationRepeat(Animator arg0) {
			
		}
		
		@Override
		public void onAnimationEnd(Animator arg0) {
			mMusicPlayListener.onAvatarChange();
			mAniAlphaAvatarShow.start();
			
		}
		
		@Override
		public void onAnimationCancel(Animator arg0) {
			
		}
	};
	
	 AnimatorListener discbgAlphaHideListener = new AnimatorListener() {
			
		@Override
		public void onAnimationStart(Animator arg0) {
			
		}
		
		@Override
		public void onAnimationRepeat(Animator arg0) {
			
		}
		
		@Override
		public void onAnimationEnd(Animator arg0) {
			mMusicPlayListener.onDiscbgChange();
			mAniAlphaDiscBgShow.start();
			
		}
		
		@Override
		public void onAnimationCancel(Animator arg0) {
			
		}
	};*/
	 
	/** 
	 * 
	 * 设置背景
	 * @param d
	 */
	public void setBackgroundDrawable(Drawable d){
		mBackground.setBackgroundDrawable(d);
	}
	
	/** 
	 * 
	 * 设置背景
	 * @param d
	 */
	public void setBackgroundResource(Bitmap bitmap){
		Bitmap bmp = GaussianBlurUtil.drawableToBitmap(new BitmapDrawable(bitmap)) ;
		mBackground.setBackgroundDrawable(GaussianBlurUtil.BoxBlurFilter(bmp));
	
	}
	public void setBackgroundResource(Drawable drawable)
	{
		//Bitmap bmp=GaussianBlurUtil.drawableToBitmap(drawable);
		//Drawable gaussianDrawable=GaussianBlurUtil.BoxBlurFilter(bmp);
		mBackground.setBackground(drawable);
	}
	public void setAvatarImageResource(Bitmap bitmap){
		if(bitmap!=null)
			mAvatar.setImageDrawable(new BitmapDrawable(bitmap));
		else {
			mAvatar.setImageResource(R.drawable.default_item_cover);
		}
	}
	
	/**
	 * 播放
	 */
	AnimatorSet animSet = new AnimatorSet();  
	public void play(){
		initNeedleAnimation(0f);
		
//        animSet.playTogether(mAniAvatar,mAniDisc);
        animSet.play(mAniAvatar).after(mAniNeedle);  
        animSet.start();  
		setPlay(true);
	}
	
	/**
	 * 暂停
	 */
	public void pause(){
		initNeedleAnimation(NEEDLE_RADIUS);
		mAniNeedle.start();
		animSet.cancel();
//		mAniDisc.cancel();
		initAvatarAnimation(mValueAvatar);
//		initDiscAnimation(mValueDisc);
		setPlay(false);
	}
	
	/**
	 * 下一首
	 */
	public void next(Bitmap bitmap){
//		mAniAlphaAvatarHide.start();
//		mAniAlphaDiscBgHide.start();
		changeImage(bitmap);
//		pause();
//		initAvatarAnimation(0f);
//		initDiscAnimation(0f);
//		initNeedleAnimation(0f);
//		play();
	}
	
	private void changeImage(final Bitmap bitmap){
		postDelayed(new Runnable() {
			
			@Override
			public void run() {
				//setBackgroundResource(bitmap);
				setAvatarImageResource(bitmap);
				
			}
		}, 0);
	}
	
	private Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if( background == null ) {   
           return null;   
        }   
  
        int bgWidth = background.getWidth();   
        int bgHeight = background.getHeight();   
        //int fgWidth = foreground.getWidth();   
        //int fgHeight = foreground.getHeight();   
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图   
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.RGB_565);  
        Canvas cv = new Canvas(newbmp);   
        //draw bg into   
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg   
        //draw fg into   
        cv.drawBitmap(foreground, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        //save all clip   
        cv.save(Canvas.ALL_SAVE_FLAG);//保存   
        //store   
        cv.restore();//存储   
        return newbmp;   
   }
	
	/**
	 * 上一首
	 */
	public void previous(Bitmap bitmap){
		
//		mAniAlphaAvatarHide.start();
//		mAniAlphaDiscBgHide.start();
		pause();
		changeImage(bitmap);
//		pause();
//		initAvatarAnimation(0f);
//		initDiscAnimation(0f);
//		initNeedleAnimation(0f);
//		play();
	}
	
	public boolean isPlay() {
		return isPlay;
	}

	public void setPlay(boolean isPlay) {
		this.isPlay = isPlay;
	}
	
	/**
	 * 初始化旋转封面动画对象
	 * @param start
	 */
	private void initAvatarAnimation(float start){
		mAniAvatar = ObjectAnimator.ofFloat(mAvatar, "rotation", start, 360f + start);
		mAniAvatar.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mValueAvatar = (Float) animation.getAnimatedValue("rotation");
				//Log.e("", "角度 : "+ mValueAvatar);
			}
		});
		mAniAvatar.setDuration(ROTATE_TIME);
		mAniAvatar.setInterpolator(new LinearInterpolator());
		mAniAvatar.setRepeatCount(ROTATE_COUNT);
		
	}
	
	/**
	 * 初始化旋转磁盘动画对象
	 * @param start
	 */
	/*private void initDiscAnimation(float start){
		mAniDisc = ObjectAnimator.ofFloat(mDisc, "rotation", start, 360f + start);
		mAniDisc.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mValueDisc = (Float) animation.getAnimatedValue("rotation");
			}
		});
		mAniDisc.setDuration(ROTATE_TIME);
		mAniDisc.setInterpolator(new LinearInterpolator());
		mAniDisc.setRepeatCount(ROTATE_COUNT);
		
	}*/
	
	/**
	 * 初始化唱针动画
	 * @param start
	 */
	private void initNeedleAnimation(float start) {
		mAniNeedle = ObjectAnimator.ofFloat(mNeedle, "rotation", start, NEEDLE_RADIUS-start).setDuration(NEEDLE_TIME);
	}
} 

