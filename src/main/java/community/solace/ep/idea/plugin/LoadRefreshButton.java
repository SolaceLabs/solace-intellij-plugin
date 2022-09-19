package community.solace.ep.idea.plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.NonBlockingReadAction;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.ui.AnimatedIcon;
import com.intellij.util.concurrency.AppExecutorUtil;

import community.solace.ep.idea.plugin.settings.AppSettingsState;
import community.solace.ep.idea.plugin.utils.TokenHolder;
import community.solace.ep.wrapper.EventPortalWrapper;

public class LoadRefreshButton extends AnAction {

	public interface Observer {
		public void refreshEventPortalData();
	}
	
    private AtomicBoolean hasBeenClicked = new AtomicBoolean(false);
    private AtomicBoolean hasBeenClickedUpdate = new AtomicBoolean(false);  // to prevent the spinning loading graphic from reloading if clicked again
    private volatile boolean firstTimeLoading = true;
    private Set<Observer> observers = new HashSet<>();

    private static final Logger logger = Logger.getInstance(LoadRefreshButton.class);
	
    protected LoadRefreshButton() {
        super("Load/Refresh Event Portal Data", "This action will connect to Solace PubSub+ Event Portal and receive all info", AllIcons.Actions.Execute);
    }
    
    public void addListener(Observer observer) {
    	observers.add(observer);
    }
    
    private void notifyObservers() {
    	for (Observer observer : observers) {
    		try {
    			observer.refreshEventPortalData();
    		} catch (RuntimeException e) {
    			logger.warn("Caught when trying to update observer: "+observer.getClass().getCanonicalName());
    			logger.warn(e);
    		}
    	}
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
    	firstTimeLoading = false;
    	if (!hasBeenClicked.compareAndSet(false, true)) return;
    	hasBeenClickedUpdate.set(true);  // tell update() to change the icon
    	@NotNull NonBlockingReadAction<Void> action = ReadAction.nonBlocking(new Runnable() {
			@Override
			public void run() {
				try {
					if (AppSettingsState.getInstance().tokenId.isEmpty()) {
						Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", "Configure Token in Settings", NotificationType.WARNING));
						return;
					}
//			        EventPortalWrapper.INSTANCE.setToken(TokenHolder.props.getProperty("token"));
					EventPortalWrapper.INSTANCE.setToken(AppSettingsState.getInstance().tokenId);
					if (EventPortalWrapper.INSTANCE.loadAll(AppExecutorUtil.getAppExecutorService())) {
						Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", "Successfully loaded all domains and objects", NotificationType.INFORMATION));
						notifyObservers();  // tell my listeners to redraw
					} else {  // problem loading!
						Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", "Had some issue loading: "+EventPortalWrapper.INSTANCE.getLoadErrorString(), NotificationType.WARNING));
					}
				} catch (RuntimeException e) {
					Notifications.Bus.notify(new Notification("ep20", "Solace Event Portal", e.toString(), NotificationType.ERROR));
				} finally {
					hasBeenClicked.set(false);
					update(event);
				}
			}
		});
    	action.submit(AppExecutorUtil.getAppExecutorService());
    }
    
    
    @Override
    public void update(AnActionEvent event) {  // update the icon?
    	
    	StringBuilder sb = new StringBuilder("AnActionEvent: ");
    	sb.append("inputEvent=").append(event.getInputEvent());
    	sb.append("; modifiers=").append(event.getModifiers());
    	sb.append("; place=").append(event.getPlace());
    	sb.append("; isFromActionToolbar=").append(event.isFromActionToolbar());
    	sb.append("; isInInjectedContext=").append(event.isInInjectedContext());
    	sb.append("; getPresentation().getTextWithMnemonic=").append(event.getPresentation().getTextWithMnemonic());
    	
//		Notifications.Bus.notify(new Notification("ep20", "PubSub+ Event Portal", sb.toString(), NotificationType.INFORMATION));
    	
    	if (firstTimeLoading) {
    		event.getPresentation().setIcon(AllIcons.Actions.Execute);
    	} else {
	    	if (hasBeenClicked.get()) {  // downloading...
	    		// only change the graphic once
	    		if (!hasBeenClickedUpdate.compareAndSet(true, false)) return;
//	    		event.getPresentation().setIcon(AllIcons.Actions.Download);
//	    		event.getPresentation().setEnabled(false);
	    		event.getPresentation().setIcon(new AnimatedIcon.Default());  // the loading icon
	    		AnimatedIcon loading = new AnimatedIcon.Default();
//	    		loading.setIn
	    	} else {
//	    		event.getPresentation().setIcon(AllIcons.Actions.Refresh);
	    		event.getPresentation().setIcon(AllIcons.Actions.BuildLoadChanges);
	    		event.getPresentation().setEnabled(true);
	    	}
    	}
    }
    
    public class EPProgress extends ProgressIndicatorBase {

		private static final long serialVersionUID = 1L;

		
		
    	
    }

}
