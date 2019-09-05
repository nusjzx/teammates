import {Component, Input, OnInit} from '@angular/core';
import {FeedbackSessionStudentResponse} from "../../../types/api-output";

@Component({
  selector: 'tm-instructor-session-no-response-panel',
  templateUrl: './instructor-session-no-response-panel.component.html',
  styleUrls: ['./instructor-session-no-response-panel.component.scss']
})
export class InstructorSessionNoResponsePanelComponent implements OnInit {

  @Input() noResponseStudentsResponse: FeedbackSessionStudentResponse[] = [];

  isTabExpanded:Boolean = false;

  constructor() { }

  ngOnInit() {
  }

  expandTab(): void {
    this.isTabExpanded = !this.isTabExpanded;
  }

}
