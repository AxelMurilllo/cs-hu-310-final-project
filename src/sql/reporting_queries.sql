/* Put your final project reporting queries here */
USE cs_hu_310_final_project;

-- Example (remove before submitting)
-- Get all students
SELECT
    *
FROM students;

-- 1. Calculate the GPA for student given a student_id (use student_id=1)
SELECT students.first_name, students.last_name, 
COUNT(class_registrations.class_registration_id) AS number_of_classes,
SUM(convert_to_grade_point(grades.letter_grade)) AS total_grade_points_earned,
avg(convert_to_grade_point(grades.letter_grade)) AS GPA
FROM students, class_registrations, grades 
WHERE students.student_id = class_registrations.student_id 
AND grades.grade_id = class_registrations.grade_id
AND students.student_id = 1;

-- 2. Calculate the GPA for each student (across all classes and all terms)
SELECT students.first_name AS first_name,
students.last_name AS last_name,
COUNT(class_registrations.class_section_id) AS number_of_classes,
SUM(convert_to_grade_point(grades.letter_grade)) AS total_grade_points_earned,
AVG(convert_to_grade_point(grades.letter_grade)) AS GPA
FROM class_sections
JOIN class_registrations ON class_registrations.class_section_id = class_sections.class_section_id
JOIN students ON students.student_id = class_registrations.student_id
JOIN grades ON grades.grade_id = class_registrations.grade_id
GROUP BY students.student_id;

-- 3. Calculate the avg GPA for each class
SELECT classes.code AS code,
classes.name AS name,
COUNT(class_registrations.class_section_id) AS number_of_grades,
SUM(convert_to_grade_point(letter_grade)) AS total_grade_points,
AVG(convert_to_grade_point(letter_grade)) AS AVG_GPA
FROM class_sections
JOIN classes ON classes.class_id = class_sections.class_id
JOIN class_registrations ON class_registrations.class_section_id = class_sections.class_section_id
JOIN grades ON grades.grade_id = class_registrations.grade_id
GROUP BY classes.class_id;

-- 4. Calculate the avg GPA for each class and term
SELECT classes.code AS code,
classes.name AS name,
terms.name AS term,
COUNT(class_registrations.class_section_id) AS number_of_grades,
SUM(convert_to_grade_point(letter_grade)) AS total_grade_points,
AVG(convert_to_grade_point(letter_grade)) AS AVG_GPA
FROM class_sections
JOIN classes ON classes.class_id = class_sections.class_id
JOIN class_registrations ON class_registrations.class_section_id = class_sections.class_section_id
JOIN terms ON terms.term_id = class_sections.term_id
JOIN grades ON grades.grade_id = class_registrations.grade_id
GROUP BY class_registrations.class_section_id;

-- 5. List all the classes being taught by an instructor (use instructor_id=1)
SELECT instructors.first_name, instructors.last_name, 
academic_titles.title AS title, classes.code, classes.name AS class_name,
terms.name AS term
FROM instructors, classes, academic_titles, terms, class_sections
WHERE instructors.academic_title_id = academic_titles.academic_title_id
AND terms.term_id = class_sections.term_id
AND instructors.instructor_id = class_sections.instructor_id
AND class_sections.class_id = classes.class_id
AND instructors.instructor_id = 1;

-- 6. List all classes with terms & instructor
SELECT classes.code AS code, classes.name AS name, terms.name AS term,
instructors.first_name AS first_name, instructors.last_name AS last_name
FROM class_sections
JOIN classes ON classes.class_id = class_sections.class_id
JOIN instructors ON instructors.instructor_id = class_sections.instructor_id
JOIN terms ON terms.term_id = class_sections.term_id
GROUP BY classes.class_id, terms.term_id, instructors.first_name, instructors.last_name;

-- 7. Calculate the remaining space left in a class
SELECT classes.code AS code, classes.name AS name, terms.name AS term,
COUNT(students.student_id) AS enrolled_students, 
(classes.maximum_students - COUNT(students.student_id)) AS space_remaining
FROM class_sections
JOIN classes ON classes.class_id = class_sections.class_id
JOIN class_registrations ON class_registrations.class_section_id = class_sections.class_section_id
JOIN students ON students.student_id = class_registrations.student_id
JOIN terms ON terms.term_id = class_sections.term_id
GROUP BY classes.class_id, terms.term_id;