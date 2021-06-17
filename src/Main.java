import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends JFrame implements MouseListener, MouseMotionListener {
    public static final long serialVersionUID=1L;
    private int x1,x2,y1,y2;
    private int x3,y3,w3,h3;
    private BufferedImage image;
    private final Vector<Rectangle> rectangles= new Vector<>();
    private boolean isNewRect= true;

    private Main() throws IOException {
        super("Crop image");
        setLayout(null);

        JFileChooser chooser= new JFileChooser();
        FileNameExtensionFilter filter =new FileNameExtensionFilter("PNG, JPG & GIF Images", "jpg", "gif","png");

        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(Paths.get(System.getProperty("user.home")+"/pictures").toFile());
        chooser.showOpenDialog(this);

        File file= new File(chooser.getSelectedFile().toURI());
        //URL url =getClass().getClassLoader().getResource(chooser.getSelectedFile().getAbsoluteFile().toString());

        image= ImageIO.read(file);
        int w = image.getWidth();
        int h = image.getHeight();

        ImageIcon ic= new ImageIcon(image);

        JLabel label = new JLabel(ic);
        label.setBounds(0,0, w+10, h+10);
        getContentPane().add(label);

        JButton button =new JButton("Crop");
        //button.setPreferredSize(new Dimension(50,10));
        button.setBounds(200, h +10, w /6,50);
        button.addActionListener(e -> {
            try {
                BufferedImage im = image.getSubimage(x3, y3 - 30, w3, h3);
                JOptionPane.showMessageDialog(null,new JLabel(new ImageIcon(im)));
                JFileChooser cc = new JFileChooser();
                cc.showSaveDialog(this);

                File f =cc.getSelectedFile();

                ImageIO.write(im,"png",f);
            }catch (RasterFormatException eve){
                JOptionPane.showMessageDialog(null,"Out Of Segment!");
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        /**JButton btn= new JButton("Open");
        btn.setBounds(400,h+10,w/6,50);
        btn.addActionListener(e -> {
            JFileChooser choo=new JFileChooser();
            choo.getFileFilter();
            choo.showOpenDialog(this);
            label
        });*/
        getContentPane().add(button);
        //getContentPane().add(btn);
        addMouseListener(this);
        addMouseMotionListener(this);

        setSize(w+50, h +300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.x1=e.getX();
        this.y1=e.getY();
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.x2=e.getX();
        this.y2=e.getY();
        Rectangle rectangle=getRectanglesFromPoint();
        this.rectangles.add(rectangle);
        this.x1=this.y1=this.x2=this.y2=0;
        this.isNewRect=true;
        repaint();
    }

    private Rectangle getRectanglesFromPoint() {
        int width =this.x1-this.x2;
        int height =this.y1-this.y2;
        return new Rectangle(width <0? this.x1: this.x2,
                height <0? this.y1: this.y2,Math.abs(width),Math.abs(height));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.x2=e.getX();
        this.y2=e.getY();
        this.isNewRect=false;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        repaint();
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        Rectangle rect=getRectanglesFromPoint();
        if (!this.isNewRect){
            g.drawRect(rect.x,rect.y,rect.width,rect.height);
        }
        if (rectangles.size()==2){
            rectangles.removeElementAt(0);
        }
        for (Rectangle rects:rectangles){
            g.drawRect(rects.x,rects.y,rects.width,rects.height);

            x3=rects.x;
            y3=rects.y;
            w3=rects.width;
            h3=rects.height;
        }
    }

    public static void main(String[] args) throws IOException {
        Main main= new Main();
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
