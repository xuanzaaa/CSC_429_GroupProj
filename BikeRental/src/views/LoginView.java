// specify the package
package views;

// system imports
import java.awt.*;
import java.awt.Font;
import java.awt.Graphics;
import java.util.*;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

// project imports
import impres.impresario.IModel;
import models.LocaleStore;

/** The class containing the Teller View  for the ATM application */
//==============================================================
public class LoginView extends View
{
    //private MainFrame mainFrame;
    // GUI stuff
    private JTextField userid;
    private JPasswordField password;
    private JButton submitButton;

    // For showing error message
    //private MessageView statusLog;

    // constructor for this class -- takes a model object
    //----------------------------------------------------------
    public LoginView(IModel clerk)
    {

        super(clerk, "LoginView");
//        Locale currentLocale = (Locale)myModel.getState("CurrentLocale");

        // set the layout for this panel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // create our GUI components, add them to this panel
        add(createTitle());
        add(createDataEntryFields());
        add(createNavigationButtons());

        //setMinimumSize(new Dimension(350, 250));
        //setMaximumSize(new Dimension(450, 350));

        // Error message area
        //add(createStatusLog("                          "));

        populateFields();

        // STEP 0: Be sure you tell your model what keys you are interested in

    }

    @Override
    public void manageSubscriptions()
    {
        myModel.subscribe("LoginError", this);
    }

    // Overide the paint method to ensure we can set the focus when made visible
    //-------------------------------------------------------------
    public void paint(Graphics g)
    {
        super.paint(g);
        userid.requestFocus();
    }

    // Create the labels and fields
    //-------------------------------------------------------------
    protected JPanel createSubTitle()
    {
        return null;
    }

    // Create the main data entry fields
    //-------------------------------------------------------------
    private JPanel createDataEntryFields()
    {
        JPanel temp = new JPanel();
        // set the layout for this panel
        temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));

        // data entry fields
        JPanel temp1 = new JPanel();
        temp1.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel useridLabel = new JLabel(messages.getString("userId"));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(4, 6, 0, 4);
        temp1.add(useridLabel, c);


        userid = new JTextField(20);
        userid.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 4, 4, 4);
        temp1.add(userid,c);

        JLabel passwordLabel = new JLabel(messages.getString("password"));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(4, 6, 0, 4);
        temp1.add(passwordLabel, c);


        password = new JPasswordField(20);
        password.addActionListener(this); 		//Adds listener for when you hit the enter key
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 4, 4, 4);
        temp1.add(password, c);

        temp.add(temp1);

        return temp;
    }

    // Create the navigation buttons
    //-------------------------------------------------------------
    protected JPanel createNavigationButtons()
    {
        JPanel temp = new JPanel();		// default FlowLayout is fine
        FlowLayout f1 = new FlowLayout(FlowLayout.CENTER);
        f1.setVgap(15);
        f1.setHgap(25);
        temp.setLayout(f1);

        // create the buttons, listen for events, add them to the panel
        submitButton = new JButton(messages.getString("login"));
        submitButton.addActionListener(this);
        temp.add(submitButton);

        return temp;
    }
    //-------------------------------------------------------------
    public void populateFields()
    {
        userid.setText("");
        password.setText("");
    }

    // IMPRESARIO: Note how we use this method name instead of 'actionPerformed()'
    // now. This is because the super-class View has methods for both action and
    // focus listeners, and both of them delegate to this method. So this method
    // is called when you either have an action (like a button click) or a loss
    // of focus (like tabbing out of a textfield, moving your cursor to something
    // else in the view, etc.)
    // process events generated from our GUI components
    //-------------------------------------------------------------
    public void processAction(EventObject evt)
    {
        // DEBUG: System.out.println("TellerView.actionPerformed()");

        //clearErrorMessage();

        String useridEntered = userid.getText();

        if ((useridEntered == null) || (useridEntered.length() == 0))
        {
            displayMessage(messages.getString("enterUserIDErrorMessage"));
            userid.requestFocus();
        }
        else
        {
            char[] passwordValueEntered = password.getPassword();
            String passwordEntered = new String(passwordValueEntered);

            for (int cnt = 0; cnt < passwordValueEntered.length; cnt++)
            {
                passwordValueEntered[cnt] = 0;
            }

            processUserIDAndPassword(useridEntered, passwordEntered);
        }

    }

    /**
     * Process userid and pwd supplied when Submit button is hit.
     * Action is to pass this info on to the teller object
     */
    //----------------------------------------------------------
    private void processUserIDAndPassword(String useridString,
                                          String passwordString)
    {
        Properties props = new Properties();
        props.setProperty("ID", useridString);
        props.setProperty("Password", passwordString);

        // clear fields for next time around
        userid.setText("");
        password.setText("");

        myModel.stateChangeRequest("Login", props);
    }

    //---------------------------------------------------------
    public void updateState(String key, Object value)
    {
        // STEP 6: Be sure to finish the end of the 'perturbation'
        // by indicating how the view state gets updated.
        if (key.equals("LoginError") == true && !value.equals(""))
        {
            // display the passed text
            displayMessage((String)value);
        }

    }
}

