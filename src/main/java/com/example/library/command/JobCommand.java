package com.example.library.command;

import com.example.library.job.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class JobCommand {

    private final List<Job> jobs;

    /**
     * Chạy một background job theo tên.
     *
     * Ví dụ: job run overdue-fine-block
     */
    @ShellMethod(key = "job run", value = "Chạy background job theo tên. Usage: job run <jobName>")
    public String run(@ShellOption(help = "Tên job, ví dụ: overdue-fine-block") String jobName) {
        Job job = jobs.stream()
                .filter(j -> j.name().equalsIgnoreCase(jobName))
                .findFirst()
                .orElse(null);

        if (job == null) {
            String available = jobs.stream()
                    .map(Job::name)
                    .collect(Collectors.joining(", "));
            return String.format(
                    "Không tìm thấy job '%s'. Các job hiện có: %s",
                    jobName,
                    available.isBlank() ? "(không có)" : available
            );
        }

        try {
            job.execute();
            return String.format("Đã chạy xong job '%s'", job.name());
        } catch (Exception e) {
            return String.format("Job '%s' thất bại: %s", job.name(), e.getMessage());
        }
    }

    /**
     * Liệt kê các background job đã đăng ký.
     *
     * Ví dụ: job list
     */
    @ShellMethod(key = "job list", value = "Liệt kê các background job đã đăng ký")
    public String list() {
        if (jobs.isEmpty()) {
            return "Chưa có job nào được đăng ký.";
        }
        return "Các job hiện có:\n" + jobs.stream()
                .map(j -> "- " + j.name())
                .collect(Collectors.joining("\n"));
    }
}
