import javax.swing.*;
class Mainframe{

    public static void main(String args[]){
       JFrame frame = new JFrame("My First GUI");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(300,300);
       JButton button = new JButton("Press");
       frame.getContentPane().add(button); // Adds Button to content pane of frame
       frame.setVisible(true);
    }
}

// import java.awt.*;

// import javax.swing.*;

// public class Mainframe imports JFrame{
//     public void initialize{
//         JFrame miniPanel = new JFrame();
//         miniPanel.setLayout(new BorderLeyout());

//         setTitle("Welcome");
//         setSize(500,600);
//         setMinimumSize(new Dimension(300,400));
//         setDefaultCloseOperation(WindowsConstants.EXIT_ON_CLOSE);
//         setVisible(true);
//     }
// }