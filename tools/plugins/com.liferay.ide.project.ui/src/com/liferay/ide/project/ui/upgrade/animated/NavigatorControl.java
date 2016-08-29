
package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageActionListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class NavigatorControl extends AbstractCanvas implements SelectionChangedListener
{
    private static final int DEFAULT_TIMER_INTERVAL = 10;

    public static final int NONE = -1;

    public static final int PAGE_WIDTH = 400;

    public static final int PAGE_HEIGHT = 120;
    public static final int BORDER = 30;
    private static final int EXIT = NONE - 1;

    private static final int BACK = EXIT - 1;
    private static final int NEXT = BACK - 1;

    private static final int CHOICES = NEXT - 1;
    private boolean overflow;
    private boolean oldShowOverlay;
    private int hover = NONE;
    private final Image[] backImages = new Image[2];
    private final Image[] nextImages = new Image[2];
    private int buttonR;
    private int answerY;
    private Rectangle backBox;
    private Rectangle nextBox;
    private int pageY;
    private Rectangle[] actionBoxes;
    
    private Display display; 

    private int select = 0;

    private final List<PageNavigatorListener> naviListeners =
        Collections.synchronizedList( new ArrayList<PageNavigatorListener>() );

    private final List<PageActionListener> actionListeners =
        Collections.synchronizedList( new ArrayList<PageActionListener>() );

    private final Runnable runnable = new Runnable()
    {
        public void run()
        {
            doRun();
        }
    };

    public NavigatorControl( Composite parent, int style )
    {
        super( parent, style | SWT.DOUBLE_BUFFERED );

        display = getDisplay();

        setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );

        addFocusListener( new FocusListener()
        {
            public void focusGained( FocusEvent e )
            {
                redraw();
            }

            public void focusLost( FocusEvent e )
            {
                redraw();
            }
        } );

        addPaintListener( new PaintListener()
        {
            @Override
            public void paintControl( PaintEvent e )
            {
                Image buffer = new Image( display, getBounds() );

                GC canvasGc = e.gc;
                // not blink
                GC bufferGC = new GC( buffer );

                bufferGC.setAdvanced( true );
                bufferGC.setBackground( canvasGc.getBackground() );
                bufferGC.fillRectangle( buffer.getBounds() );

                paint( bufferGC );

                canvasGc.drawImage( buffer, 0, 0 );

                bufferGC.dispose();
                buffer.dispose();

                scheduleRun();
            }
        } );

        addMouseTrackListener( new MouseTrackAdapter()
        {

            @Override
            public void mouseExit( MouseEvent e )
            {
                onMouseMove( Integer.MIN_VALUE, Integer.MIN_VALUE );
            }
        } );

        addMouseMoveListener( new MouseMoveListener()
        {

            public void mouseMove( MouseEvent e )
            {
                onMouseMove( e.x, e.y );
            }
        } );

        addMouseListener( new MouseAdapter()
        {

            @Override
            public void mouseDown( MouseEvent e )
            {
                // left button
                if( e.button == 1 )
                {
                    onMouseDown( e.x, e.y );
                }
            }
        } );

        init();

        scheduleRun();
    }

    protected boolean actionOnMouseDown( int x, int y )
    {
        int i = getAction( x, y );

        if( i != NONE )
        {
            doAction( i );

            return true;
        }

        return false;
    }

    protected int actionOnMouseMove( int x, int y )
    {
        int i = getAction( x, y );

        if( i != NONE )
        {
            //pageBufferUpdated = false;
            return CHOICES - i;
        }

        if( hover <= CHOICES )
        {
            //pageBufferUpdated = false;
        }

        return NONE;
    }

    public void addPageActionListener( PageActionListener listener )
    {
        this.actionListeners.add( listener );
    }

    public void addPageNavigateListener( PageNavigatorListener listener )
    {
        this.naviListeners.add( listener );
    }

    protected boolean advance()
    {
        if( overflow )
        {
            overflow = false;
        }

        boolean showOverlay = shouldShowOverlay();

        if( showOverlay != oldShowOverlay )
        {
            oldShowOverlay = showOverlay;
        }

        return true;
    }

    private void doAction( int i )
    {
        Page page = getSelectedPage();

        PageAction[] pageActions = page.getActions();

        PageAction targetAction = pageActions[i];

        boolean originState = targetAction.isSelected();

        targetAction.setSelected( !originState );

        if( originState )
        {
            page.setSelectedAction( null );
        }
        else
        {
            page.setSelectedAction( targetAction );

            for(int j = 0 ; j < pageActions.length ; j++)
            {
                if(j != i)
                {
                    pageActions[j].setSelected( false );
                }
            }

            if( page.showNextPage() )
            {
                PageActionEvent event = new PageActionEvent();

                event.setAction( targetAction );

                event.setTargetPage( UpgradeView.getPage( select + 1 ) );

                for( PageActionListener listener : actionListeners )
                {
                    listener.onPageAction( event );
                }
            }
        }
    }

    protected synchronized void doRun()
    {
        if( isDisposed() )
        {
            return;
        }

        boolean needsRedraw = advance();

        if( needsRedraw )
        {
            redraw();
        }
        else
        {
            scheduleRun();
        }
    }

    public final int getAction( int x, int y )
    {
        PageAction[] actions = getSelectedPage().getActions();

        for( int i = 0; i < actions.length; i++ )
        {
            Rectangle box = actionBoxes[i];

            if( box != null && box.contains( x, y ) )
            {
                return i;
            }
        }

        return NONE;
    }


    private Page getSelectedPage()
    {
        return UpgradeView.getPage( select );
    }

    @Override
    protected void init()
    {
        super.init();

        backImages[0] = loadImage( "back.png" );
        backImages[1] = loadImage( "back_hover.png" );

        nextImages[0] = loadImage( "next.png" );
        nextImages[1] = loadImage( "next_hover.png" );

        buttonR = nextImages[0].getBounds().height / 2;

        answerY = 5 + buttonR;

        actionBoxes = new Rectangle[2];

    }

    protected boolean onMouseDown( int x, int y )
    {
        boolean retVal = false;

        if( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            Page page = getSelectedPage();

            if( page != null )
            {
                PageNavigateEvent event = new PageNavigateEvent();

                if( page.showBackPage() && backBox != null && backBox.contains( x, y ) )
                {
                    event.setTargetPage( UpgradeView.getPage( select - 1 ) );

                    retVal = true;
                }

                if( page.showNextPage() && nextBox != null && nextBox.contains( x, y ) )
                {
                    event.setTargetPage( UpgradeView.getPage( select + 1  ) );

                    retVal = true;
                }

                if( retVal == true )
                {

                    for( PageNavigatorListener listener : naviListeners )
                    {
                        listener.onPageNavigate( event );
                    }
                }

                if( actionOnMouseDown( x, y ) )
                {
                    return true;
                }
            }
        }

        return retVal;
    }

    protected boolean onMouseMove( int x, int y )
    {
        if( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            Page page = getSelectedPage();

            if( page != null )
            {
                if( page.showBackPage() && backBox != null && backBox.contains( x, y ) )
                {
                    hover = BACK;
                    return true;
                }

                if( page.showNextPage() && nextBox != null && nextBox.contains( x, y ) )
                {
                    hover = NEXT;
                    return true;
                }

                hover = actionOnMouseMove( x, y );

                if( hover != NONE )
                {
                    return true;
                }
            }
        }

        hover = NONE;

        return false;
    }

    @Override
    public void onSelectionChanged( int targetSelection )
    {
        select = targetSelection;
    }

    private void paint( GC gc )
    {
        gc.setFont( getBaseFont() );
        gc.setLineWidth( 3 );
        gc.setAntialias( SWT.ON );

        Page page = getSelectedPage();

        backBox = null;
        nextBox = null;

        if( page.showBackPage() )
        {
            backBox = drawImage( gc, backImages[hover == BACK ? 1 : 0], getBounds().width / 2 - 200, answerY );
        }

        if( page.showNextPage() )
        {
            nextBox = drawImage( gc, nextImages[hover == NEXT ? 1 : 0], getBounds().width / 2 + 200, answerY );
        }

        //oldHover = hover;

        paintActions( gc, page );
    }

    public Rectangle paintAction( GC gc, int index, int x, int y, boolean hovered, boolean selected, PageAction action )
    {
        Image[] images = action.getImages();

        Image image = images[0];

        if( hovered )
        {
            image = images[2];
        }
        else if( selected )
        {
            image = images[1];
        }

        return drawImage( gc, image, x ,y );
    }

    private void paintActions( GC gc, Page page )
    {
        PageAction[] actions = page.getActions();

        boolean selecteds[] = new boolean[actions.length];
        boolean hovereds[] = new boolean[actions.length];

        Point sizes[] = new Point[actions.length];

        //int width = ( actions.length - 1 ) * BORDER;
        int height = 0;

        for( int i = 0; i < actions.length; i++ )
        {
            selecteds[i] = actions[i].isSelected();

            if( CHOICES - i == hover )
            {
                //oldHover = hover;
                hovereds[i] = true;
            }

            sizes[i] = actions[i].getSize();
            //width += sizes[i].x;
            height = Math.max( height, sizes[i].y );
        }

        int x = getBounds().width/2 - 40;

        int y = answerY - pageY;

        for( int i = 0; i < actions.length; i++ )
        {
            PageAction action = actions[i];

            actionBoxes[i] = paintAction( gc, i, x, y, hovereds[i], selecteds[i], action );

            x = getBounds().width/2 + 40;
        }
    }

    private void scheduleRun()
    {
        display.timerExec( DEFAULT_TIMER_INTERVAL, runnable );
    }

    protected boolean shouldShowOverlay()
    {
        return ( System.currentTimeMillis() / 1000 & 1 ) == 1;
    }
}
