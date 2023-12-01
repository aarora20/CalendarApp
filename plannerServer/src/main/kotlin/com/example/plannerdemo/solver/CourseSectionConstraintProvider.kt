package com.example.plannerdemo.solver


import com.example.plannerdemo.domain.CourseSection
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore
import org.optaplanner.core.api.score.stream.Constraint
import org.optaplanner.core.api.score.stream.ConstraintFactory
import org.optaplanner.core.api.score.stream.ConstraintProvider
import org.optaplanner.core.api.score.stream.Joiners
import org.optaplanner.examples.common.experimental.ExperimentalConstraintCollectors
import org.optaplanner.examples.common.experimental.api.ConsecutiveIntervalInfo
import org.optaplanner.examples.common.experimental.api.IntervalBreak
import java.time.Duration
import java.time.LocalTime


class TimeTableConstraintProvider : ConstraintProvider {

    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint> {
        return arrayOf(
            minimizeGaps(constraintFactory),
            minimizeWorkingDays(constraintFactory),
            noMismatchCourseComponent(constraintFactory),
            noOverlappingCourseSections(constraintFactory),
        )
    }

    fun minimizeGaps(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach(CourseSection::class.java)
            .groupBy({ section -> section.getSelectedTimeslot()?.getDayOfWeek()  },
                ExperimentalConstraintCollectors.consecutiveTemporalIntervals(
                    { section -> section.getSelectedTimeslot()?.getStartTime() }
                ) { section -> section.getSelectedTimeslot()?.getEndTime() })
            .flattenLast<IntervalBreak<CourseSection, LocalTime?, Duration?>> { obj: ConsecutiveIntervalInfo<CourseSection, LocalTime?, Duration> -> obj.getBreaks() }
            .penalize(HardMediumSoftScore.ONE_SOFT) { a, b -> b.getLength()!!.toMinutes().toInt() }
            .asConstraint("Minimize Gaps")
    }

    fun minimizeWorkingDays(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach<CourseSection>(CourseSection::class.java)
            .groupBy<Any> { section: CourseSection ->
                section.getSelectedTimeslot()?.getDayOfWeek()
            }
            .penalize(HardMediumSoftScore.ONE_MEDIUM).asConstraint("Minimize Working Days")
    }

    fun noOverlappingCourseSections(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEachUniquePair(
            CourseSection::class.java,
            Joiners.equal { section: CourseSection ->
                section.getSelectedTimeslot()?.getDayOfWeek()
            },
            Joiners.overlapping(
                 { section: CourseSection ->
                    section.getSelectedTimeslot()?.getStartTime()
                },
                 { section: CourseSection ->
                    section.getSelectedTimeslot()?.getEndTime()
                })
        )
            .penalize(HardMediumSoftScore.ofHard(2)).asConstraint("Overlapping sections")
    }

    fun noMismatchCourseComponent(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEachUniquePair(
            CourseSection::class.java,
            Joiners.equal { section: CourseSection ->
                section.getCourseSectionName()
            }
        ).filter { section1, section2 ->
            section1.getSelectedTimeslot()?.getComponentId() != section2.getSelectedTimeslot()?.getComponentId()

        }
            .penalize(HardMediumSoftScore.ONE_HARD).asConstraint("Mismatch Course Component")
    }
}