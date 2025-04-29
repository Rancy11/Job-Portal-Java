package model;

public class ApplicantBST {
    private TreeNode root;

    public void insert(Applicant applicant) {
        root = insertRec(root, applicant);
    }

    private TreeNode insertRec(TreeNode root, Applicant applicant) {
        if (root == null) {
            root = new TreeNode(applicant);
            return root;
        }
        if (applicant.getEmail().compareTo(root.applicant.getEmail()) < 0) {
            root.left = insertRec(root.left, applicant);
        } else if (applicant.getEmail().compareTo(root.applicant.getEmail()) > 0) {
            root.right = insertRec(root.right, applicant);
        }
        return root;
    }

    public Applicant search(String email) {
        return searchRec(root, email);
    }

    private Applicant searchRec(TreeNode root, String email) {
        if (root == null || root.applicant.getEmail().equals(email)) {
            return root != null ? root.applicant : null;
        }
        if (email.compareTo(root.applicant.getEmail()) < 0) {
            return searchRec(root.left, email);
        }
        return searchRec(root.right, email);
    }
}