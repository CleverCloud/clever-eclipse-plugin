package com.clevercloud.eclipse.plugin.ui;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CommitDialog extends TitleAreaDialog {

	private Text txtCommitMessage;
	private String commitMessage;

	public CommitDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Commit message");
		setMessage("Please enter your commit message.", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		createCommitMessage(container);
		return area;
	}

	private void createCommitMessage(Composite container) {
		Label lbtCommitMessage = new Label(container, SWT.NONE);
		lbtCommitMessage.setText("Commit Message");

		GridData dataCommitMessage = new GridData();
		dataCommitMessage.grabExcessHorizontalSpace = true;
		dataCommitMessage.horizontalAlignment = GridData.FILL;

		txtCommitMessage = new Text(container, SWT.BORDER);
		txtCommitMessage.setLayoutData(dataCommitMessage);
	}

	private void saveInput() {
		this.commitMessage = this.txtCommitMessage.getText();
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	public String getCommitMessage() {
		return this.commitMessage;
	}

	@Override
	public void okPressed() {
		saveInput();
		super.okPressed();
	}
}
