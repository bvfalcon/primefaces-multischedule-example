/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.primefaces.model.ScheduleEvent;

import name.bychkov.primefaces.model.DefaultMultiScheduleModel;
import name.bychkov.primefaces.model.MultiScheduleModel;

@ManagedBean
@ViewScoped
public class MultiScheduleView implements Serializable {

	private MultiScheduleModel eventModel;

	private ScheduleEvent<?> event = new DefaultScheduleEvent<>();

	@PostConstruct
	public void init() {
		eventModel = new DefaultMultiScheduleModel();

		DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder().title("Champions League Match")
				.startDate(previousDay8Pm()).endDate(previousDay11Pm()).description("Team A vs. Team B")
				.url("https://www.uefa.com/uefachampionsleague/").build();
		eventModel.addEvent("First", event);

		event = DefaultScheduleEvent.builder().title("Birthday Party").startDate(today1Pm()).endDate(today6Pm())
				.description("Aragorn").overlapAllowed(true).build();
		eventModel.addEvent("First", event);
		
		event = DefaultScheduleEvent.builder().title("Geburtstag").startDate(today1Pm()).endDate(today6Pm())
				.description("Gimley").overlapAllowed(true).build();
		eventModel.addEvent("Second", event);

		event = DefaultScheduleEvent.builder().title("Breakfast at Tiffanys (always resizable)").startDate(nextDay9Am())
				.endDate(nextDay11Am()).description("all you can eat").overlapAllowed(true)
				/* .resizable(true) */.build();
		eventModel.addEvent("Second", event);

		event = DefaultScheduleEvent.builder().title("Plant the new garden stuff (always draggable)")
				.startDate(theDayAfter3Pm()).endDate(fourDaysLater3pm()).description("Trees, flowers, ...")
				/* .draggable(true) */.build();
		eventModel.addEvent("Second", event);

		DefaultScheduleEvent<?> scheduleEventAllDay = DefaultScheduleEvent.builder().title("Holidays (AllDay)")
				.startDate(sevenDaysLater0am()).endDate(eightDaysLater0am()).description("sleep as long as you want")
				.allDay(true).build();
		eventModel.addEvent("First", scheduleEventAllDay);
	}

	public LocalDateTime getRandomDateTime(LocalDateTime base) {
		LocalDateTime dateTime = base.withMinute(0).withSecond(0).withNano(0);
		return dateTime.plusDays(((int) (Math.random() * 30)));
	}

	public MultiScheduleModel getEventModel() {
		return eventModel;
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
			System.out.println(event);
			//eventModel.addEvent(event);
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