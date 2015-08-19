package com.clevercloud.eclipse.plugin.core;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleUtils {

	public static MessageConsole getConsole(String name) {
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = consolePlugin.getConsoleManager();
		IConsole[] consoles = consoleManager.getConsoles();

		for (IConsole console : consoles) {
			if (console.getName().equals(name))
				return (MessageConsole) console;
		}

		MessageConsole newConsole = new MessageConsole(name, null);
		consoleManager.addConsoles(new IConsole[]{newConsole});
		return newConsole;
	}

	public static void printMessage(String consoleName, String message) {
		MessageConsole console = getConsole(consoleName);
		MessageConsoleStream stream = console.newMessageStream();
		stream.println(message);
	}

	public static void showConsole(String name) {
		MessageConsole console = getConsole(name);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IConsoleView view;
		try {
			view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			view.display(console);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public static boolean consoleExist(String name) {
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = consolePlugin.getConsoleManager();
		IConsole[] consoles = consoleManager.getConsoles();
		for (IConsole console : consoles) {
			if (console.getName().equals(name))
				return true;
		}
		return false;
	}
}
