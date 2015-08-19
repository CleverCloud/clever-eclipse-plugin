package com.clevercloud.eclipse.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clevercloud.eclipse.plugin.api.CcApi;

public class AppLinkerHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		if (!CcApi.isAuthentified()) {
			CcApi.getInstance().executeLogin(shell);
			if (!CcApi.isAuthentified())
				return null;
		}
		//TODO: Invoke wizard with ImportSelecionPage & lin
		return null;
	}
}
