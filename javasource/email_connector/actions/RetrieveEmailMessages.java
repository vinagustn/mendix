// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package email_connector.actions;

import com.mendix.core.CoreException;
import com.mendix.datahub.connector.email.model.ReceiveEmailAccount;
import com.mendix.datahub.connector.email.service.EmailServiceWorker;
import com.mendix.datahub.connector.email.utils.EmailConnectorException;
import com.mendix.datahub.connector.email.utils.Error;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.webui.CustomJavaAction;
import email_connector.implementation.EmailListener;
import email_connector.implementation.MxMailMapper;
import encryption.proxies.microflows.Microflows;
import static email_connector.implementation.Commons.getProtocol;

public class RetrieveEmailMessages extends CustomJavaAction<java.lang.Void>
{
	private IMendixObject __emailAccount;
	private email_connector.proxies.EmailAccount emailAccount;
	private java.lang.String onEmailFetchMicroflow;
	private java.lang.String onFetchCompleteMicroflow;
	private java.lang.String onFetchErrorMicroflow;

	public RetrieveEmailMessages(IContext context, IMendixObject emailAccount, java.lang.String onEmailFetchMicroflow, java.lang.String onFetchCompleteMicroflow, java.lang.String onFetchErrorMicroflow)
	{
		super(context);
		this.__emailAccount = emailAccount;
		this.onEmailFetchMicroflow = onEmailFetchMicroflow;
		this.onFetchCompleteMicroflow = onFetchCompleteMicroflow;
		this.onFetchErrorMicroflow = onFetchErrorMicroflow;
	}

	@java.lang.Override
	public java.lang.Void executeAction() throws Exception
	{
		this.emailAccount = this.__emailAccount == null ? null : email_connector.proxies.EmailAccount.initialize(getContext(), __emailAccount);

		// BEGIN USER CODE
		if (this.emailAccount == null)
			throw new EmailConnectorException(Error.EMPTY_EMAIL_ACCOUNT.getMessage());
		if (Boolean.FALSE.equals(this.emailAccount.getisIncomingEmailConfigured()) || this.emailAccount.getIncomingEmailConfiguration_EmailAccount() == null)
			throw new EmailConnectorException(Error.EMPTY_INCOMING_EMAIL_CONFIG.getMessage());

		var serverAccount = new ReceiveEmailAccount(getProtocol(this.emailAccount.getIncomingEmailConfiguration_EmailAccount().getIncomingProtocol()), this.emailAccount.getIncomingEmailConfiguration_EmailAccount().getServerHost(), this.emailAccount.getIncomingEmailConfiguration_EmailAccount().getServerPort(), this.emailAccount.getUsername(), Microflows.decrypt(getContext(), this.emailAccount.getPassword()));
		MxMailMapper.setReceiveAccountConfigurations(this.emailAccount, serverAccount);
		var emailListener = new EmailListener(this.onEmailFetchMicroflow, this.onFetchCompleteMicroflow, this.onFetchErrorMicroflow, this.emailAccount);
		var emailService = new EmailServiceWorker(serverAccount);
		emailService.registerOnFetchEmailListener(emailListener);
		emailService.fetchEmailsInBatch();

		return null;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "RetrieveEmailMessages";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
