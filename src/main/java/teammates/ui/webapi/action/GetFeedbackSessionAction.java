package teammates.ui.webapi.action;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.FeedbackSessionData;
import teammates.ui.webapi.request.Intent;

/**
 * Get a feedback session.
 */
public class GetFeedbackSessionAction extends BasicFeedbackSubmissionAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
        case STUDENT_RESULT:
            if (userInfo == null && StringHelper.isEmpty(getRequestParamValue(Const.ParamsNames.REGKEY))) {
                throw new UnauthorizedAccessException("Instructor or Student account is required to access this resource.");
            }
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            break;
        case FULL_DETAIL:
            if (userInfo == null) {
                throw new UnauthorizedAccessException("Instructor account is required to access this resource.");
            }
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            if (userInfo == null) {
                throw new UnauthorizedAccessException("Instructor account is required to access this resource.");
            }
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        FeedbackSessionData response = new FeedbackSessionData(feedbackSession);

        switch (intent) {
        case STUDENT_SUBMISSION:
        case INSTRUCTOR_SUBMISSION:
        case STUDENT_RESULT:
        case INSTRUCTOR_RESULT:
            // hide some attributes for submission
            response.hideInformationForStudent();
            break;
        case FULL_DETAIL:
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        return new JsonResult(response);
    }
}
