package org.chrisbailey.todo;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class PreferenceManager 
{
    private static final String BACKGROUND_DRAWABLE_PREFIX = "background";
    private static final String ACTIVE_DRAWABLE_PREFIX = "icon_active";
    private static final String FINISHED_DRAWABLE_PREFIX = "icon_finished";
    
    private static final String LOG_TAG = "ReferenceManager";

    private int currentBackground = -1;    
    private int currentBackgroundRef = -1;
    private int currentIcon = -1;
    private int activeIconRef = -1;
    private int finishedIconRef = -1;
    private int currentActiveColor = -1;
    private int currentFinishedColor = -1;
    private int currentSize = -1;
    
    public PreferenceManager(Context c, ToDoDatabase db)
    {
        setBackground(db.getPrefBackground());

        setIcons(db.getPrefIcon());
        
        int i = db.getPrefColorActive();
        if (i == 0) i = c.getResources().getColor(R.color.default_active_color);
        setActiveColor(i);
        
        i = db.getPrefColorFinished();
        if (i == 0) i = c.getResources().getColor(R.color.default_finished_color);
        setFinishedColor(i);
        
        i = db.getPrefSize();
        if (i == -1) i = (int) new TextView(c).getTextSize();
        setSize(i);
    }
    
    /**
     * Save state to database
     * @param db
     */
    public void save(ToDoDatabase db)
    {
    	Log.i(LOG_TAG, "Saving: bg:" + currentBackground + " icon:" + currentIcon + " Acolor:"+currentActiveColor+" Fcolor:"+currentFinishedColor+" size:"+currentSize);
    	db.setPrefBackground(currentBackground);
    	db.setPrefColorActive(currentActiveColor);
    	db.setPrefColorFinished(currentFinishedColor);
    	db.setPrefIcon(currentIcon);
    	db.setPrefSize(currentSize);
    }
    
    public void setSize(int i)
    {
    	currentSize = i;
    }
    
    public int getSize()
    {
    	return currentSize;
    }
    
    public int getTitleSize()
    {
    	return currentSize+2;
    }
        
    public int getActiveColor()
    {
    	return currentActiveColor;
    }
    
    public void setActiveColor(int i)
    {
    	currentActiveColor = i;
    }
    
    public int getFinishedColor()
    {
    	return currentFinishedColor;
    }
    
    public void setFinishedColor(int i)
    {
    	currentFinishedColor = i;
    }
    
    int[] getAllBackgrounds()
    {
    	return getAllDrawables(BACKGROUND_DRAWABLE_PREFIX);
    }
    
    public int[] getAllIcons()
    {
    	return getAllDrawables(ACTIVE_DRAWABLE_PREFIX);
    }
    
    private int[] getAllDrawables(String str)
    {
        ArrayList <Integer> drawables = new ArrayList<Integer>();

        try {
            Field f[] = R.drawable.class.getFields();

            for (int i = 0; i < f.length; i++)
            {
                if (f[i].getName().startsWith(str))
                {
                    drawables.add(f[i].getInt(null));
                }
            }
         }
         catch (Throwable e) {
            Log.e(LOG_TAG,"Error obtaining drawable",e);
         }
         
        int [] drawableReferences = new int[drawables.size()];
        int i = 0;
        for (Integer integer : drawables) drawableReferences[i++] = integer;
        
        return drawableReferences;
    }
    
    public void setBackground(int i)
    {
    	if (i < 0) i = 0;
    	currentBackground = i;
    	currentBackgroundRef = getBackgroundId(currentBackground);
    }
    public void setIcons(int i)
    {
    	if (i < 0) i = 0;
    	currentIcon = i;
    	activeIconRef = getActiveIconId(currentIcon);
    	finishedIconRef = getFinishedIconId(currentIcon);
    }

    /**
     * Returns the reference to the current background drawable
     * @return
     */
    public int getBackground()
    {
    	return currentBackgroundRef;
    }
    
    /**
     * Returns the current background file number.
     * E.g. background2.png => 2
     * @return
     */
    public int getBackgroundId()
    {
    	return currentBackground;
    }
    
    public int getIconId()
    {
    	return currentIcon;
    }
    
    public int getActiveIcon()
    {
    	return activeIconRef;
    }
    public int getFinishedIcon()
    {
    	return finishedIconRef;
    }

    private static int getActiveIconId(int i)
    {
        DrawableField f = new DrawableField(ACTIVE_DRAWABLE_PREFIX);
        return f.getDrawableField(i);
    }

    private static int getFinishedIconId(int i)
    {
        DrawableField f = new DrawableField(FINISHED_DRAWABLE_PREFIX);
        return f.getDrawableField(i);
    }
    private static int getBackgroundId(int i)
    {
        DrawableField f = new DrawableField(BACKGROUND_DRAWABLE_PREFIX);
        return f.getDrawableField(i);
    }
    
    public static class DrawableField
    {
        String field;
        
        public DrawableField(String s)
        {
            field = s;
        }
        
        public int getDrawableField(int i)
        {
            try 
            {
                i++;
                return R.drawable.class.getField(field+i).getInt(null);
            } catch (Exception e)
            {
                Log.e(LOG_TAG,"Error obtaining drawable",e);
            }
            return -1;
        }
    }
    
}
