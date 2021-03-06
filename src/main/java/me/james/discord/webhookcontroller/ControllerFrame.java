package me.james.discord.webhookcontroller;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.io.*;
import org.apache.http.*;

public class ControllerFrame extends JFrame
{
    public static ArrayList<EmbedContent> embeds = new ArrayList<>();
    private JTextField webhookLink = new JTextField();
    private JTextField username = new JTextField();
    private JTextArea message = new JTextArea();
    private JCheckBox tts = new JCheckBox( "TTS" );
    private JButton sendBtn = new JButton( "Send Webhook Request" );
    private JButton addEmbedBtn = new JButton( "Add Embed" );

    public ControllerFrame()
    {
        setSize( 650, 270 );
        setLocationRelativeTo( null );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        setTitle( "Discord Webhook Controller" );
        setResizable( false );
        setLayout( null );
        webhookLink.setSize( 560, 20 );
        webhookLink.setLocation( 80, 3 );
        username.setSize( 150, 20 );
        username.setLocation( 80, 28 );
        message.setSize( getWidth(), 175 );
        message.setLocation( 0, 50 );
        message.setLineWrap( true );
        message.setFont( Font.getFont( "Arial" ) );
        JLabel webhookLabel = new JLabel( "Webhook Link" );
        webhookLabel.setSize( 100, 20 );
        webhookLabel.setLocation( 5, 5 );
        JLabel usernameLabel = new JLabel( "Username" );
        usernameLabel.setSize( 100, 20 );
        usernameLabel.setLocation( 5, 25 );
        JLabel authorLabel = new JLabel( "© 2017 James Puleo (click for more info)" );
        authorLabel.setForeground( Color.BLUE );
        authorLabel.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        authorLabel.setSize( 200, 20 );
        authorLabel.setLocation( 3, 223 );
        authorLabel.addMouseListener( new MouseListener()
        {
            @Override
            public void mouseClicked( MouseEvent e )
            {
                if ( Desktop.isDesktopSupported() )
                    try
                    {
                        Desktop.getDesktop().browse( URI.create( "https://github.com/electricman226" ) );
                    } catch ( IOException e1 )
                    {
                        e1.printStackTrace();
                    }
            }

            @Override
            public void mousePressed( MouseEvent e )
            {

            }

            @Override
            public void mouseReleased( MouseEvent e )
            {

            }

            @Override
            public void mouseEntered( MouseEvent e )
            {

            }

            @Override
            public void mouseExited( MouseEvent e )
            {

            }
        } );
        sendBtn.setSize( 250, 26 );
        sendBtn.setLocation( 280, 24 );
        sendBtn.addActionListener( e ->
        {
            if ( webhookLink.getText().isEmpty() || ( message.getText().isEmpty() && embeds.size() == 0 ) )
                return;
            allowInput( false );
            new Thread( () ->
            {
                try
                {
                    HttpResponse response;
                    if ( embeds.size() > 0 )
                        response = WebhookUtils.makeWebhookRequest( webhookLink.getText(), message.getText(), tts.isSelected(), username.getText(), embeds.toArray( new EmbedContent[embeds.size()] ) );
                    else
                        response = WebhookUtils.makeWebhookRequest( webhookLink.getText(), message.getText(), tts.isSelected(), username.getText() );
                    if ( response != null )
                    {
                        String content = ( response.getEntity() != null ? IOUtils.toString( response.getEntity().getContent() ) : null );
                        JOptionPane.showMessageDialog( this, "The request returned status " + response.getStatusLine().toString() + ", with " + ( content != null ? "the following content:\n" + content : "no content." ), "Request Response", JOptionPane.INFORMATION_MESSAGE );
                        if ( response.getStatusLine().getStatusCode() < 300 )
                        {
                            message.setText( "" );
                            embeds.clear();
                        }
                    }
                } catch ( IOException | IllegalArgumentException e1 )
                {
                    WebhookUtils.displayStackTrace( e1 );
                    e1.printStackTrace();
                } finally
                {
                    allowInput( true );
                }
            }, "webhookRequestThread" ).start();
        } );
        addEmbedBtn.setSize( 111, 26 );
        addEmbedBtn.setLocation( 530, 24 );
        addEmbedBtn.setToolTipText( "Ctrl + Click to clear all embeds." );
        addEmbedBtn.addActionListener( e ->
        {
            if ( ( ( e.getModifiers() & ActionEvent.CTRL_MASK ) == ActionEvent.CTRL_MASK ) )
            {
                embeds.clear();
                JOptionPane.showMessageDialog( this, "Cleared all embeds.", "Embeds cleared", JOptionPane.INFORMATION_MESSAGE );
                return;
            }
            new AddEmbedFrame();
        } );
        tts.setSize( 45, 20 );
        tts.setLocation( 233, 27 );
        add( webhookLabel );
        add( authorLabel );
        add( usernameLabel );
        add( message );
        add( username );
        add( webhookLink );
        add( sendBtn );
        add( addEmbedBtn );
        add( tts );
        setVisible( true );
    }

    private void allowInput( boolean in )
    {
        webhookLink.setEnabled( in );
        username.setEnabled( in );
        message.setEnabled( in );
        tts.setEnabled( in );
        sendBtn.setEnabled( in );
        addEmbedBtn.setEnabled( in );
    }
}
