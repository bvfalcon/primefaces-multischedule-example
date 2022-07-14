package org.primefaces.showcase.view.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {

	private ScheduleModel eventModel;

	private ScheduleModel lazyEventModel;

	private ScheduleEvent<?> event = new DefaultScheduleEvent<>();

	@PostConstruct
	public void init() {
		eventModel = new DefaultScheduleModel();

		DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder().title("Champions League Match")
				.startDate(previousDay8Pm()).endDate(previousDay11Pm()).description("Team A vs. Team B")
				.url("https://www.uefa.com/uefachampionsleague/").build();
		eventModel.addEvent(event);

		event = DefaultScheduleEvent.builder().title("Birthday Party").startDate(today1Pm()).endDate(today6Pm())
				.description("Aragon").overlapAllowed(true).build();
		eventModel.addEvent(event);

		event = DefaultScheduleEvent.builder().title("Breakfast at Tiffanys (always resizable)").startDate(nextDay9Am())
				.endDate(nextDay11Am()).description("all you can eat").overlapAllowed(true)
				/* .resizable(true) */.build();
		eventModel.addEvent(event);

		event = DefaultScheduleEvent.builder().title("Plant the new garden stuff (always draggable)")
				.startDate(theDayAfter3Pm()).endDate(fourDaysLater3pm()).description("Trees, flowers, ...")
				/* .draggable(true) */.build();
		eventModel.addEvent(event);

		DefaultScheduleEvent<?> scheduleEventAllDay = DefaultScheduleEvent.builder().title("Holidays (AllDay)")
				.startDate(sevenDaysLater0am()).endDate(eightDaysLater0am()).description("sleep as long as you want")
				.allDay(true).build();
		eventModel.addEvent(scheduleEventAllDay);

		lazyEventModel = new LazyScheduleModel() {

			@Override
			public void loadEvents(LocalDateTime start, LocalDateTime end) {
				for (int i = 1; i <= 5; i++) {
					LocalDateTime random = getRandomDateTime(start);
					addEvent(DefaultScheduleEvent.builder().title("Lazy Event " + i).startDate(random)
							.endDate(random.plusHours(3)).build());
				}
			}
		};
	}

	public LocalDateTime getRandomDateTime(LocalDateTime base) {
		LocalDateTime dateTime = base.withMinute(0).withSecond(0).withNano(0);
		return dateTime.plusDays(((int) (Math.random() * 30)));
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}

	private LocalDateTime previousDay8Pm() {
		return LocalDateTime.now().minusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime previousDay11Pm() {
		return LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime today1Pm() {
		return LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime theDayAfter3Pm() {
		return LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime today6Pm() {
		return LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime nextDay9Am() {
		return LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime nextDay11Am() {
		return LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime fourDaysLater3pm() {
		return LocalDateTime.now().plusDays(4).withHour(15).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime sevenDaysLater0am() {
		return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime eightDaysLater0am() {
		return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

	public LocalDate getInitialDate() {
		return LocalDate.now().plusDays(1);
	}

	public ScheduleEvent<?> getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent<?> event) {
		this.event = event;
	}

	public void addEvent() {
		if (event.isAllDay()) {
			// see https://github.com/primefaces/primefaces/issues/1164
			if (event.getStartDate().toLocalDate().equals(event.getEndDate().toLocalDate())) {
				event.setEndDate(event.getEndDate().plusDays(1));
			}
		}

		if (event.getId() == null)
			eventModel.addEvent(event);
		else
			eventModel.updateEvent(event);

		event = new DefaultScheduleEvent<>();
	}

	public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
		event = selectEvent.getObject();
	}

	public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
		event = DefaultScheduleEvent.builder().startDate(selectEvent.getObject())
				.endDate(selectEvent.getObject().plusHours(1)).build();
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved",
				"Delta:" + event.getDeltaAsDuration());

		addMessage(message);
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized",
				"Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());

		addMessage(message);
	}

	public void onEventDelete() {
		String eventId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("eventId");
		if (event != null) {
			ScheduleEvent<?> event = eventModel.getEvent(eventId);
			eventModel.deleteEvent(event);
		}
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}