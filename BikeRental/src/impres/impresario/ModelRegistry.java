// tabs=4
//************************************************************
//	COPYRIGHT 2003 ArchSynergy, Ltd. - ALL RIGHTS RESERVED
//
// This file is the product of ArchSynergy, Ltd. and cannot be
// reproduced, copied, or used in any shape or form without
// the express written consent of ArchSynergy, Ltd.
//************************************************************
//
//	$Source: /cvsroot/EZVideo/impresario/ModelRegistry.java,v $
//
//	Reason: The registry mechanism for the Model object in
//			an MVC relationship.
//
//	Revision History: See end of file.
//
//*************************************************************

// JavaDoc information
/** @author		$Author: smitra $ */
/** @version	$Revision: 1.6 $ */

// specify the package
package impres.impresario;

// system imports
import impres.common.PropertyFile;
import impres.common.StringList;
import impres.event.Event;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;


// project imports

/**
 * This class is used to instantiate the object that is encapsulated
 * by every EasyObserver client in order to keep track of which control
 * subscribes to which key and which keys depend on which other keys.
 * After the client updates its state on the basis of a posted state change,
 * this class' methods are used to update the GUI controls that subscribe to
 * the keys that depend on the key on which the state change is posted.
 */
//==============================================================
public class ModelRegistry extends Registry
{
	// data members

	/** A list of keys that are dependant on other keys */
	private Properties myDependencies;

	// Class constructor
	//----------------------------------------------------------
	public ModelRegistry(final String classname, 			// the name of the class that contains this Registry, debug only
			final Properties dependencies)	// the dependency information for keys
	{
		super(classname);	// build our base class

		// save our dependencies
		myDependencies = dependencies;
	}


	// Class constructor
	//----------------------------------------------------------
	public ModelRegistry(final String classname, 			// the name of the class that contains this Registry, debug only
			final String dependencyFile)		// filename that contains the dependency information for keys
	{
		super(classname);	// build our base class

		// save our dependencies
		myDependencies = new PropertyFile(dependencyFile);
	}

	// Class constructor
	//----------------------------------------------------------
	public ModelRegistry(final String classname)		// filename that contains the dependency information for keys
	{
		super(classname);					// build our base class
		myDependencies = new Properties();	// may be replaced later

	}


	//----------------------------------------------------------
	public void setDependencies(final Properties dependencies)		// filename that contains the dependency information for keys
	{
		myDependencies = dependencies;
	}


	/**
	 * This is the method that actually checks the dependency file for the key on which the
	 * state change was posted, finds out which keys depend on this key, and then
	 * invokes the update methods on all the Views that subscribe to each dependant key.
	 * The value sent to the View comes from this object.
	 * Note: This version of updateSubscribers is only called by a Model.
	 *
	 * @param	key		Value of key on which the state change was posted and whose
	 *					dependencies must be determined
	 */
	//----------------------------------------------------------
	public void updateSubscribers(final String key, final IModel client)
	{
		// DEBUG:System.out.println("ModelRegistry.updateSubscribers - " + key);

		// now update all the subscribers to the changed key
		final StringList propertyList = new StringList(key  + "," + myDependencies.getProperty(key));
        System.out.println(myDependencies);

		while (propertyList.hasMoreElements() == true)
		{
			// pick out each dependant property from the list
			final String dependProperty = (String)propertyList.nextElement();
			// and get the subscriber(s) to this property

			// Get all subscribers to this dependant property
			final Object tempObj = mySubscribers.get(dependProperty);

			// make sure we have subscribers
			if(tempObj == null)
			{
				// DEBUG: System.out.println("ModelRegistry[" + myClassName + "].updateSubscribers - no subscribers found for dependency " + dependProperty);
				continue;
			}

			// see if we have multiple subscribers
			if(tempObj instanceof Vector)
			{
				// get the list of elements
				final Enumeration subscriberList =((Vector)tempObj).elements();
                System.err.println(subscriberList);
                System.err.println(key);
                for (Object i: ((Vector)tempObj))
                    System.err.println(i);
				while(subscriberList.hasMoreElements() == true)
				{
					// extract each subscriber
					final Object subscriber = subscriberList.nextElement();
					// DEBUG: System.out.println("Vector Subscriber: " + subscriber.getClass());

					// update via a key-value pair
					if(subscriber instanceof IView)
					{
						// DEBUG: System.out.println("Vector IView [" + key + "] " + dependProperty + ": " + client.getState(dependProperty));
						((IView)subscriber).updateState(dependProperty, client.getState(dependProperty));
					}
					else
					{
						new Event(Event.getLeafLevelClassName(this), "UpdateSubscribers", "EVT_InvalidSubscriber", "Vector Invalid Subscriber: " + subscriber.getClass(), Event.WARNING);
						// DEBUG: System.err.println("ModelRegistry.updateSubscribers - Invalid Subscriber type for key = " + key + " and depend property = " + dependProperty + "!");
					}
				}
			}
			else	// we must have a single subscriber
				// If not, use the standard update via a key-value pair
				if(tempObj instanceof IView)
				{
					// DEBUG: System.out.println("IView [" + key + "] " + dependProperty + ": " + client.getState(dependProperty));
					((IView)tempObj).updateState(dependProperty, client.getState(dependProperty));
				}
				else
				{
					new Event(Event.getLeafLevelClassName(this), "UpdateSubscribers", "EVT_InvalidSubscriber", "Invalid Subscriber: " + tempObj.getClass(), Event.WARNING);
					// DEBUG: System.err.println("Registry.updateSubscribers - Invalid Subscriber type for key = " + key + " and dependProperty = " + dependProperty + "!");
				}
		}
	}

}


//**************************************************************
//	Revision History:
//
//	$Log: ModelRegistry.java,v $
//	Revision 1.6  2004/01/18 05:52:06  smitra
//	Removed a lot of debug o/p stmts
//
//	Revision 1.5  2003/10/24 17:45:32  tomb
//	Fixed base class name from Registry1 to Registry.
//
//	Revision 1.4  2003/10/24 17:31:38  tomb
//	Added default initialization of dependency properties.
//
//	Revision 1.3  2003/10/24 12:31:43  tomb
//	Updated to use Registry1 as a base class.
//
//	Revision 1.2  2003/10/24 05:45:19  tomb
//	Changed register to subscribe.
//
//	Revision 1.1  2003/10/24 04:12:07  tomb
//	The original Registry class has been split into ModelRegistry and Controlregistry to better reflect their behavrio relative to the new MVC Impresario interfaces.
//
