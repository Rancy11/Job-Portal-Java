package model;

public class TreeNode {
    Applicant applicant;
    TreeNode left, right;

    public TreeNode(Applicant applicant) {
        this.applicant = applicant;
        left = right = null;
    }
}