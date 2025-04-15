#include <wx/wx.h>

class MyApp : public wxApp
{
public:
    virtual bool OnInit() override;
};

class MyFrame : public wxFrame
{
public:
    MyFrame()
            : wxFrame(NULL, wxID_ANY, "Hello from wxWidgets!", wxDefaultPosition, wxSize(400, 300))
    {
        // Добавим кнопку просто так
        wxButton* button = new wxButton(this, wxID_ANY, "Нажми меня", wxPoint(10, 10));
        button->Bind(wxEVT_BUTTON, &MyFrame::OnButtonClicked, this);
    }

private:
    void OnButtonClicked(wxCommandEvent&)
    {
        wxMessageBox("Привет! Это wxWidgets!", "Сообщение", wxOK | wxICON_INFORMATION);
    }
};

wxIMPLEMENT_APP(MyApp);

bool MyApp::OnInit()
{
    MyFrame* frame = new MyFrame();
    frame->Show(true);
    return true;
}
