/*
 * This file is part of BOINC.
 * http://boinc.berkeley.edu
 * Copyright (C) 2020 University of California
 *
 * BOINC is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * BOINC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BOINC.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.berkeley.boinc.attach;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import edu.berkeley.boinc.R;
import edu.berkeley.boinc.utils.Logging;

public class ManualUrlInputFragment extends DialogFragment {
    private EditText urlInputET;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attach_project_manual_url_input_dialog, container, false);

        urlInputET = v.findViewById(R.id.url_input);

        Button continueButton = v.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(view -> {
            if(Logging.DEBUG) {
                Log.d(Logging.TAG, "ManualUrlInputFragment: continue clicked");
            }

            if(!checkDeviceOnline()) {
                return;
            }

            //startActivity
            Intent intent = new Intent(getActivity(), BatchConflictListActivity.class);
            intent.putExtra("conflicts", false);
            intent.putExtra("manualUrl", urlInputET.getText().toString());
            startActivity(intent);
            dismiss();
        });

        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    // check whether device is online before starting connection attempt
    // as needed for AttachProjectLoginActivity (retrieval of ProjectConfig)
    // note: available internet does not imply connection to project server
    // is possible!
    private boolean checkDeviceOnline() {
        final Activity activity = getActivity();
        assert activity != null;
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;

        final boolean online;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            online = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }
        else {
            online = connectivityManager.getActiveNetwork() != null;
        }
        if(!online) {
            Toast toast = Toast.makeText(getActivity(), R.string.attachproject_list_no_internet, Toast.LENGTH_SHORT);
            toast.show();
            if(Logging.DEBUG) {
                Log.d(Logging.TAG, "ManualUrlInputFragment not online, stop!");
            }
        }
        return online;
    }
}
